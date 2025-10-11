// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.parse.Parser;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.hivevm.railroad.RailroadHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class CodeBlock implements DocumentElement {

    private final String id;
    private final String language;
    private final List<Line> lines;
    private final boolean showLineNumbers;

    private CodeBlock(Builder builder) {
        this(builder, Stream.of(builder.code.split("\\n")).map(Line::new).toList());
    }

    private CodeBlock(Builder builder, List<Line> lines) {
        this.id = builder.id;
        this.language = builder.language;
        this.lines = lines;
        this.showLineNumbers = builder.showLineNumbers;
    }

    @Override
    public String id() {
        return id;
    }

    public List<Line> getLines() {
        return lines;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }

    @Override
    public <C> void accept(DocumentVisitor visitor, C context) {
        visitor.visit(this, context);
    }

    public record Line(List<TextSpan> span) {

        private Line(String text) {
            this(List.of(new TextSpan.Builder(text).build()));
        }
    }

    public static class Builder {

        private String id;
        private String code;
        private String language = "";
        private boolean showLineNumbers = false;

        public Builder(String id, String code) {
            this.id = id;
            this.code = code;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder showLineNumbers() {
            this.showLineNumbers = true;
            return this;
        }

        public DocumentElement build() {
            var lang = language != null ? language : "";
            return switch (lang) {
                case "jsongraph" -> {
                    Graphviz.useEngine(new GraphvizV8Engine());

                    var uml = "@start" + "json" + "\n!pragma layout smetana\n" + code + "\n@end\" + type + \"\n";
                    var reader = new SourceStringReader(uml);
                    try (var ostream = new ByteArrayOutputStream()) {
                        reader.outputImage(ostream, new FileFormatOption(FileFormat.SVG));
                        var bytes = ostream.toByteArray();
                        var base64 = Base64.getEncoder().encodeToString(bytes);
                        yield new Image(id, "data:image/svg+xml;base64," + base64);
                    } catch (IOException e) {
                    }
                    yield new CodeBlock(this);
                }
                case "uml", "ebnf", "regex" -> {
                    Graphviz.useEngine(new GraphvizV8Engine());

                    var uml = "@start" + language + "\n!pragma layout smetana\n" + code + "\n@end\" + type + \"\n";
                    var reader = new SourceStringReader(uml);
                    try (var ostream = new ByteArrayOutputStream()) {
                        reader.outputImage(ostream, new FileFormatOption(FileFormat.SVG));
                        var bytes = ostream.toByteArray();
                        var base64 = Base64.getEncoder().encodeToString(bytes);
                        yield new Image(id, "data:image/svg+xml;base64," + base64);
                    } catch (IOException e) {
                    }
                    yield new CodeBlock(this);
                }
                case "dot" -> {
                    try (var ostream = new ByteArrayOutputStream()) {
                        var graph = new Parser().read(code);
                        Graphviz.fromGraph(graph)
                                .render(Format.SVG_STANDALONE)
                                .toOutputStream(ostream);

                        var bytes = ostream.toByteArray();
                        var base64 = Base64.getEncoder().encodeToString(bytes);
                        yield new Image(id, "data:image/svg+xml;base64," + base64);
                    } catch (IOException e) {
                    }
                    yield new CodeBlock(this);
                }
                case "railroad" -> {
                    try (var ostream = new ByteArrayOutputStream()) {
                        var svg = RailroadHandler.BNF_TO_SVG.handleRequest(code, null);
                        ostream.write(svg.getBytes());

                        var bytes = ostream.toByteArray();
                        var base64 = Base64.getEncoder().encodeToString(bytes);
                        yield new Image(id, "data:image/svg+xml;base64," + base64);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    yield new CodeBlock(this);
                }
                case "ini", "yaml", "xml", "json", "java", "cpp", "rust" -> {
                    try {
                        var rows = new ArrayList<Line>();
                        TextMateGenerator.generate(lang, code, rows);
                        yield new CodeBlock(this, rows);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    yield new CodeBlock(this);
                }
                default -> new CodeBlock(this);
            };
        }
    }
}