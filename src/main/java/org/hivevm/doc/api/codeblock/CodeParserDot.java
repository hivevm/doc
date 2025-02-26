// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Renderer;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.hivevm.doc.api.builder.CodeBuilder;
import org.hivevm.doc.api.builder.CodeParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * The {@link CodeParserDot} class.
 */
class CodeParserDot implements CodeParser {

    /**
     * Generates the code text
     *
     * @param text
     * @param builder
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        try (InputStream dot = new ByteArrayInputStream(text.getBytes())) {
            MutableGraph graph = new Parser().read(dot);
            Renderer renderer = Graphviz.fromGraph(graph).render(Format.SVG_STANDALONE);
            String base64 = Base64.getEncoder().encodeToString(renderer.toString().getBytes());
            String dataUri = "data:image/svg+xml;base64," + base64;
            builder.addImage(dataUri, null, null, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            builder.addText(text);
        }
    }
}
