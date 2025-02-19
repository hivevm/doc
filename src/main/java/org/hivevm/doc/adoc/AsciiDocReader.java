// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.adoc;

import org.hivevm.util.MergeReader;
import org.hivevm.util.Replacer;
import org.hivevm.util.Resolver;

import java.io.*;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * The {@link AsciiDocReader} implements a reader based on MARKDOWN. The Reader starts from an
 * {@link InputStream} an includes referenced files to create a single huge MARKDOWN file.
 */
public class AsciiDocReader extends MergeReader {

    // Parameters like: :author: [AUTHOR] or lines <<<
    private static final Pattern IGNORE =
            Pattern.compile("^(\\:[^\\:]+\\:|<<<).*", Pattern.CASE_INSENSITIVE);

    // File includes: include::(RELATIVE_PATH)[leveloffset=OFFSET]
    private static final Pattern INCLUDE =
            Pattern.compile("^include::([^\\.]+\\.adoc)(?:\\[leveloffset=\\+(\\d+)\\])?", Pattern.CASE_INSENSITIVE);

    // Header definition: =+ HEADER
    private static final Pattern HEADER = Pattern.compile("^([=]+)\\s*(.+)",
            Pattern.CASE_INSENSITIVE);

    // Block definition: =+ HEADER
    private static final Pattern BLOCK = Pattern.compile("^\\[([^\\]]+)\\]",
            Pattern.CASE_INSENSITIVE);

    // Footnote definition: [ID]: URL "TITLE"
    private static final Pattern FOOTNOTE =
            Pattern.compile("^\\[(\\w+)\\]:\\s([^\\s]+)(\\s\".+\")?", Pattern.CASE_INSENSITIVE);

    // Footnote reference: [TEXT]?[ID]
    private static final Pattern FOOTNOTE_REF =
            Pattern.compile("(?:\\[([^\\]]*)\\])\\[(\\w+)\\]", Pattern.CASE_INSENSITIVE);

    // Image definition: ![](IMAGE_PATH)
    private static final Pattern IMAGE = Pattern.compile("!\\[([^\\]]*)\\]\\(([^\\)]+)\\)",
            Pattern.CASE_INSENSITIVE);

    /**
     * Constructs an instance of {@link AsciiDocReader}.
     *
     * @param reader
     * @param resolver
     */
    public AsciiDocReader(Reader reader, Resolver resolver) {
        super(reader, resolver);
    }

    /**
     * Merge the files to the markdown content.
     *
     * @param title
     * @param header
     * @param reader
     * @param resolver
     * @param request
     */
    protected final void merge(String title, String header, Reader reader, Resolver resolver, Request request)
            throws IOException {
        String codeName = null;
        String codeBlock = null;

        String level = header;
        String name = UUID.randomUUID().toString();
        try (BufferedReader buffered = new BufferedReader(reader)) {
            for (String line : buffered.lines().collect(Collectors.toList())) {
                // Parsing Code
                if (codeName != null) {
                    if (codeBlock == null) {
                        request.writeln("~~~" + codeName);
                        codeBlock = line;
                    } else if (codeBlock.equals(line)) {
                        request.writeln("~~~");
                        codeBlock = null;
                        codeName = null;
                    } else
                        request.writeln(line);

                    continue;
                } // Literal Blocks
                else if ("----".equals(line) || "....".equals(line)) {
                    request.writeln("~~~");
                    codeBlock = line;
                    codeName = "";

                    continue;
                }

                // Remove + at end of line
                if (line.endsWith("+")) {
                    line = line.substring(0, line.length() - 1);
                }

                // Ignore lines that starting with a tab
                if (line.startsWith("\t")) {
                    request.writeln(line);
                    continue;
                }

                // Ignore parameters
                Matcher matcher = IGNORE.matcher(line);
                if (matcher.find())
                    continue;

                // Processes file includes.
                matcher = AsciiDocReader.INCLUDE.matcher(line);
                if (matcher.find()) {
                    String path = matcher.group(1);
                    if (resolver.exists(path)) {
                        String hash = matcher.group(2) == null ? level : "#####".substring(0, Integer.parseInt(matcher.group(2)));
                        try (Reader r = new InputStreamReader(resolver.getInputStream(path))) {
                            merge(title, "\n" + hash, r, resolver.getResolver(path), request);
                        }
                    }
                    continue;
                }

                // Update headers
                matcher = AsciiDocReader.HEADER.matcher(line);
                if (matcher.find()) {
                    level = header + matcher.group(1);
                    level = level.replaceAll("=", "#");
                    if (matcher.group(1).equals("#") && (title != null)) {
                        request.writeln(level + " " + title);
                    } else {
                        request.writeln(level + " " + matcher.group(2));
                    }
                    continue;
                }

                // Catch blocks
                matcher = AsciiDocReader.BLOCK.matcher(line);
                if (matcher.find()) {
                    String[] params = matcher.group(1).split(",");
                    if (params.length == 2 && "source".equalsIgnoreCase(params[0]))
                        codeName = params[1];
                    if ("plantuml".equalsIgnoreCase(params[0]))
                        codeName = "uml";

                    continue;
                }

                // Update footnotes
                matcher = AsciiDocReader.FOOTNOTE.matcher(line);
                if (matcher.find()) {
                    request.addFootNote(name, matcher.group(1), matcher.group(2), matcher.group(3));
                    continue;
                }

                // Replace all link references
                line = Replacer.replaceAll(line, AsciiDocReader.FOOTNOTE_REF,
                        m -> request.getFootNote(name, m.group(2), m.group(1)));

                // Normalize image paths
                line = Replacer.replaceAll(line, AsciiDocReader.IMAGE,
                        m -> m.group(2).startsWith("/") || m.group(2).startsWith("data:") ? m.group(0)
                                : String.format("![%s](%s)", m.group(1), resolver.getPath(m.group(2))));

                request.writeln(line);
            }
        }
    }
}
