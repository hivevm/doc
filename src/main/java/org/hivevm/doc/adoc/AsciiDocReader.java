// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.adoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.hivevm.util.MergeReader;
import org.hivevm.util.Resolver;


/**
 * The {@link AsciiDocReader} implements a reader based on MARKDOWN. The Reader starts from an
 * {@link InputStream} an includes referenced files to create a single huge MARKDOWN file.
 */
public class AsciiDocReader extends MergeReader {

    // Parameters like: :author: [AUTHOR] or lines <<<
    private static final Pattern IGNORE = Pattern.compile("^(:[^:]+:|<<<).*");

    // File includes: include::(RELATIVE_PATH)[leveloffset=OFFSET]
    private static final Pattern INCLUDE = Pattern.compile("^include::([^\\[]+)(?:\\[leveloffset=\\+(\\d+)])?", Pattern.CASE_INSENSITIVE);

    // Option definition: [KEY="VALUE",KEY="VALUE"]
    private static final Pattern OPTIONS = Pattern.compile("^\\[([^]]+)](.*)");
    private static final Pattern OPTION_ALERT = Pattern.compile("^(IMPORTANT|NOTE|TIP|WARNING):");
    private static final Pattern OPTION_PARAMS = Pattern.compile("([^=,]+)(?:=\"?([^\"]+)\"?)?");

    // Header definition: =+ HEADER
    private static final Pattern HEADER = Pattern.compile("^(=+)\\s*(.+)");

    // Hard break: \s*\\
    private static final Pattern HARDBREAK = Pattern.compile("(\\s?\\+$)");

    // Block definition: \s+BLOCK
    private static final Pattern BLOCK = Pattern.compile("^(\\s+)(\\w+.*)");

    // List definition: 1.|a.|x.|-|* TEXT  or ..|--|**|
    private static final Pattern LIST = Pattern.compile("^(\\s*)(?:(-+)|(\\*+)|(\\.+)|(\\d+)\\.|([a-z])\\.)\\s(.*)");

    // Image definition: image::IMAGE_PATH[OPTIONS]
    private static final Pattern IMAGE = Pattern.compile("^image::([^\\[]+)\\[([^\\[]*)]", Pattern.CASE_INSENSITIVE);

    private static final Pattern EMPHASIS = Pattern.compile("\\*([^*]+)\\*|\\*\\*([^*]+)\\*\\*");

    private static final Pattern LINK = Pattern.compile("\\W?(\\w+://[\\w.\\-/?%#=]+)(?:\\[([^]]*)])?");
    private static final Pattern LINK_HEADER = Pattern.compile("(<<([_#][^>]+)>>)");

    private static final Map<String, String> ALERTS = Map.of("important", "s", "warning", "w", "note", "n", "tip", "s");


    private enum Kind {NUMBER, WORD, SIGN, STAR}

    private record Item(Kind code, int intend, int offset) {
    }

    /**
     * Constructs an instance of {@link AsciiDocReader}.
     */
    public AsciiDocReader(Reader reader, Resolver resolver) {
        super(reader, resolver);
    }

    private class AsciiRequest {

        private final AsciiRequest root;
        private final String   title;
        private final Request  request;
        private final Resolver resolver;

        private boolean alert;
        private boolean intend;
        private String codeBlock;

        private int    header;
        private List<String> tableCols;
        private int    tableSize;

        private final Stack<Item>  list    = new Stack<>();
        private       Map<String,String> options = Collections.emptyMap();

        private AsciiRequest(String title, Request request, Resolver resolver, int header) {
            this.root = this;
            this.title = title;
            this.request = request;
            this.resolver = resolver;
            this.header = header;
        }

        private AsciiRequest(String path, int header, AsciiRequest parent) {
            this.root = parent.root;
            this.title = parent.title;
            this.request = parent.request;
            this.resolver = parent.resolver.getResolver(path);
            this.header = header;
        }

        private AsciiRequest child(String path, int header) {
            return new AsciiRequest(path, header, this);
        }
    }

    /**
     * Merge the files to the markdown content.
     */
    @Override
    protected final void merge(String title, int header, Reader reader, Resolver resolver, Request request)
            throws IOException {
        merge(header, reader, new AsciiRequest(title, request, resolver, header));
    }

    /**
     * Merge the files to the markdown content.
     */
    private void merge(int header, Reader reader, AsciiRequest request) throws IOException {
        try (BufferedReader buffered = new BufferedReader(reader)) {
            var lines = buffered.lines().toList();
            for (String line : lines) {
                // Processes includes.
                Matcher matcher = AsciiDocReader.INCLUDE.matcher(line);
                if (matcher.find()) {
                    String path = matcher.group(1);
                    if (request.resolver.exists(path)) {
                        int hash = matcher.group(2) == null ? request.header : Integer.parseInt(matcher.group(2));
                        try (Reader r = new InputStreamReader(request.resolver.getInputStream(path))) {
                            merge(hash, r, request.child(path, header));
                        }
                    }
                    continue;
                }

                // Ignore ASCIIDoc parameters
                matcher = AsciiDocReader.IGNORE.matcher(line);
                if (matcher.find())
                    continue;

                // Update HEADER
                line = handleHeader(line, header, request);

                handleLine(line, request);
            }
        }
    }

    /**
     * Merge the files to the markdown content.
     */
    private void handleLine(String line, AsciiRequest request) throws IOException {
        // Manage Alerts
        String option = request.options.keySet().stream().findFirst().orElse(null);
        Matcher matcher = AsciiDocReader.OPTION_ALERT.matcher(line);
        if (matcher.find()) {
            request.root.alert = true;
            request.request.write("!!" + ALERTS.getOrDefault(matcher.group(1).toLowerCase(), "i"));
            line = line.substring(matcher.end());
        }
        else if (!request.options.isEmpty() && ALERTS.containsKey(option)) {
            request.root.alert = true;
            request.request.write("!!" + ALERTS.get(option) + " ");
            request.options = Collections.emptyMap();
        } // Close Alert messages
        else if (request.root.alert && line.isEmpty()) {
            request.root.alert = false;
            request.request.writeln("!!");
        }

        if (matcher.find()) {
            request.options = new HashMap<>();
            Matcher params = OPTION_PARAMS.matcher(matcher.group(1));
            while (params.find())
                request.options.put(params.group(1).trim().toLowerCase(), params.group(2));
            if (matcher.group(2) == null || matcher.group(2).trim().isEmpty())
                return;
            line = matcher.group(2);
        }


        // Handle Code Blocks
        if (handleCode(line, request))
            return;

        // Handle Tables
        if (handleTable(line, request))
            return;

        // Catch options
        matcher = AsciiDocReader.OPTIONS.matcher(line);
        if (matcher.find()) {
            request.options = new HashMap<>();
            Matcher params = OPTION_PARAMS.matcher(matcher.group(1));
            while (params.find())
                request.options.put(params.group(1).trim().toLowerCase(), params.group(2));
            if (matcher.group(2) == null || matcher.group(2).trim().isEmpty())
                return;
            line = matcher.group(2);
        }

        // Replace + at end of line with HARD BREAKS
        matcher = AsciiDocReader.HARDBREAK.matcher(line);
        if (matcher.find())
            line = line.substring(0, matcher.start(1)) + "  ";

        // Update LIST
        line = handleList(line, request);

        // Update BLOCK
        matcher = AsciiDocReader.BLOCK.matcher(line);
        if (request.root.intend && line.isEmpty()) {
            request.request.writeln("~~~");
            request.root.intend = false;
        }
        else if (matcher.find()) {
            if (!request.root.intend)
                request.request.writeln("~~~");
            request.root.intend = true;
            request.request.writeln(matcher.group(2));
            return;
        }

        // Update images
        matcher = AsciiDocReader.IMAGE.matcher(line);
        if (matcher.find()) {
            line = String.format("![](%s)", request.resolver.getPath(matcher.group(1)));
            if (matcher.group(2) != null) {
                line += "{" + matcher.group(2).replaceAll("\"", "") + "}";
            }
        }

        line = handleLinks(line);
        line = handleHeaderLinks(line);
        line = handleStrongEmphasis(line);

        request.request.writeln(line);
    }

    private String handleHeader(String line, int header, AsciiRequest request) {
        var matcher = AsciiDocReader.HEADER.matcher(line);
        if (matcher.find()) {
            request.header = header + matcher.group(1).length();
            String intend = "############".substring(0, request.header);
            request.request.write(intend + " ");

            if ((request.title != null) && matcher.group(1).length() == 1)
                line = request.title;
            else
                line = matcher.group(2);

            request.list.clear();
            request.options = Collections.emptyMap();
        }
        return line;
    }

    private boolean handleCode(String line, AsciiRequest request) {
        // Handle Code Blocks
        if (request.root.codeBlock != null) {
            if (request.root.codeBlock.equals(line)) {
                request.request.writeln("~~~");
                request.root.codeBlock = null;
            } else
                request.request.writeln(line);
            return true;
        } // Code Blocks
        else if (line.startsWith("----") || line.startsWith("....")) {
            var format = "";
            if (!request.options.isEmpty()) {
                if (request.options.containsKey("plantuml") || request.options.containsKey("ditaa")) {
                    format = request.options.containsKey("ditaa") ? "ditaa" : "uml";
                    List<String> options = new ArrayList<>();
                    for (var key : request.options.keySet()) {
                        var o = request.options.get(key);
                        if (key.equalsIgnoreCase("align"))
                            options.add("align=" + o);
                        else if (key.equalsIgnoreCase("scaledwidth"))
                            options.add("width=" + o);
                    }
                    if (!options.isEmpty())
                        format += "{" + String.join(" ", options) + "}";
                }
                else
                    format = request.options.keySet().stream().filter(k -> !k.equals("") && !k.equals("source")).findFirst().orElse("");
            }
            request.request.writeln("~~~" + format);
            request.root.codeBlock = line;

            request.list.clear();
            request.options = Collections.emptyMap();
            return true;
        }
        return false;
    }

    private boolean handleTable(String line, AsciiRequest request) {
        if (request.tableCols != null) {
            if (line.startsWith("|===")) {
                if (!request.tableCols.isEmpty()) {
                    while (request.tableCols.size() < request.tableSize)
                        request.tableCols.add("");
                    var cols = request.tableCols.stream().map(l -> l.trim().replace("\n", "<br/>")).toList();
                    request.request.writeln("| " + String.join(" | ", cols) + " |");
                    request.tableCols = new ArrayList<>();
                }
                request.request.writeln("");
                request.tableCols = null;
                request.tableSize = 0;
                return true;
            }
            else if (request.tableSize == 0) {
                var headers = Arrays.stream(line.split("\\|"))
                        .filter(e -> !e.isEmpty()).toList();
                List<String> columns = request.options.containsKey("cols")
                        ? Stream.of(request.options.get("cols").split(",")).map(v -> "----------".substring(0, Integer.parseInt(v.substring(0, 1)))).toList()
                        : headers.stream().map(h -> "---").toList();
                request.request.writeln("| " + String.join(" | ", headers) + " |");
                request.request.writeln("| " + String.join(" | ", columns) + " |");
                request.tableCols = new ArrayList<>();
                request.tableSize = headers.size();
            }
            else if (line.isEmpty() || !line.startsWith("|")) {
                if (!request.tableCols.isEmpty()) {
                    var offset = request.tableCols.size() - 1;
                    request.tableCols.set(offset, request.tableCols.get(offset) + "\n" + line);
                }
            }
            else {
                List<String> row = Arrays.stream(line.substring(1).split("\\|"))
                        .map(String::trim).toList();
                if (request.tableCols.size() + row.size() > request.tableSize) {
                    while (request.tableCols.size() < request.tableSize)
                        request.tableCols.add("");
                    var cols = request.tableCols.stream().map(l -> l.trim().replace("\n", "<br/>")).toList();
                    request.request.writeln("| " + String.join(" | ", cols) + " |");
                    request.tableCols = new ArrayList<>();
                }
                request.tableCols.addAll(row);
            }
            return true;
        } // Table Blocks
        else if (line.startsWith("|===")) {
            request.tableCols = Collections.emptyList();
            request.tableSize = 0;
            request.list.clear();
            return true;
        }
        return false;
    }

    private String handleList(String line, AsciiRequest request) {
        // workaround
        if (line.startsWith("*") && line.endsWith("*:"))
            request.list.clear();

        var matcher = AsciiDocReader.LIST.matcher(line);
        if (matcher.find()) {
            Kind kind = Kind.SIGN;
            var intend = matcher.group(1) == null ? 0 : matcher.group(1).length();

            if (matcher.group(2) != null && matcher.group(2).length() > 1)
                intend = matcher.group(2).length();
            else if (matcher.group(3) != null) {
                kind = Kind.STAR;
                if (matcher.group(3).length() > 1)
                    intend = Math.max(intend, matcher.group(3).length());
            } else if (matcher.group(4) != null) {
                kind = Kind.NUMBER;
                if (matcher.group(4).length() > 1)
                    intend = Math.max(intend, matcher.group(4).length());
            } else if (matcher.group(5) != null)
                kind = Kind.NUMBER;
            else if (matcher.group(6) != null)
                kind = Kind.WORD;

            int index = 0;
            boolean found = false;
            while (!found && index < request.list.size()) {
                var curr = request.list.get(index);
                if (kind == curr.code && intend == curr.intend) {
                    request.list.set(index, new Item(kind, intend, curr.offset() + 1));
                    found = true;
                } else
                    index++;
            }
            if (!found)
                request.list.push(new Item(kind, intend, 1));

            while (request.list.size() > index + 1)
                request.list.pop();

            var top = request.list.peek();
            String text = "\t\t\t\t\t\t".substring(0, request.list.size() - 1);
            text += switch (top.code()) {
                case NUMBER, WORD -> top.offset() + ".";
                default -> "-";
            };
            request.request.write(text + " ");
            line = matcher.group(7);
        }
        return line;
    }

    private String handleLinks(String line) {
        int offset = 0;
        StringBuilder text = new StringBuilder();
        Matcher matcher = LINK.matcher(line);
        while (matcher.find()) {
            var hasTitle = matcher.group(2) != null && !matcher.group(2).isEmpty();
            text.append(line, offset, matcher.start(1));
            text.append("[");
            text.append(matcher.group(hasTitle ? 2 : 1));
            text.append("](");
            text.append(matcher.group(1));
            text.append(")");
            offset = matcher.end();
        }
        return text + line.substring(offset);
    }

    private String handleHeaderLinks(String line) {
        int offset = 0;
        StringBuilder text = new StringBuilder();
        Matcher matcher = LINK_HEADER.matcher(line);
        while (matcher.find()) {
            text.append(line, offset, matcher.start(1));
            text.append("[");
            text.append("](");
            text.append(line, matcher.start(2), matcher.end(2));
            text.append(")");
            offset = matcher.end(1);
        }
        return text + line.substring(offset);
    }

    private String handleStrongEmphasis(String line) {
        int offset = 0;
        StringBuilder text = new StringBuilder();
        Matcher matcher = EMPHASIS.matcher(line);
        while (matcher.find()) {
            if (matcher.group(1) == null) {
                text.append(line, offset, matcher.start(2));
                text.append(line, matcher.start(2), matcher.end(2));
                offset = matcher.end(2);
            }
            else {
                text.append(line, offset, matcher.start(1));
                text.append('*');
                text.append(line, matcher.start(1), matcher.end(1));
                text.append('*');
                offset = matcher.end(1);
            }
        }
        return text + line.substring(offset);
    }
}
