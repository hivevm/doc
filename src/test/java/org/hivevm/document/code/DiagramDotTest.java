// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.code;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.parse.Parser;
import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * The {@link DiagramDotTest} class.
 */
public class DiagramDotTest extends DiagramTest {

    protected final void handleRequest(InputStream istream, OutputStream ostream, String type) throws IOException {
        var graph = new Parser().read(istream);
        Graphviz.fromGraph(graph)
                .render(Format.SVG_STANDALONE)
                .toOutputStream(ostream);
    }

    @Test
    public void testGraphviz() throws Exception {
        var text = """
                digraph G {
                
                  subgraph cluster_0 {
                    style=filled;
                    color=lightgrey;
                    node [style=filled,color=white];
                    a0 -> a1 -> a2 -> a3;
                    label = "process #1";
                  }
                
                  subgraph cluster_1 {
                    node [style=filled];
                    b0 -> b1 -> b2 -> b3;
                    label = "process #2";
                    color=blue
                  }
                  start -> a0;
                  start -> b0;
                  a1 -> b3;
                  b2 -> a3;
                  a3 -> a0;
                  a3 -> end;
                  b3 -> end;
                
                  start [shape=Mdiamond];
                  end [shape=Msquare];
                }
                """;

        var file = new File("/tmp/graphviz.svg");
        try (var istream = new ByteArrayInputStream(text.getBytes());
             var ostream = new FileOutputStream(file)) {
            handleRequest(istream, ostream, null);
        }
    }
}