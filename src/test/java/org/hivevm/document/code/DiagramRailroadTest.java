// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.code;

import org.junit.jupiter.api.Test;

import java.io.*;

/**
 * The {@link DiagramRailroadTest} class.
 */
public class DiagramRailroadTest extends DiagramTest {

    protected final void handleRequest(InputStream istream, OutputStream ostream, String type) throws IOException {
        var text = new String(istream.readAllBytes());
        var svg = org.hivevm.railroad.RailroadHandler.BNF_TO_SVG.handleRequest(text, null);
        ostream.write(svg.getBytes());
    }

    @Test
    public void testRailroad() throws Exception {
        var text = """
                    H2_SELECT =
                        'SELECT' [ 'TOP' term ] [ 'DISTINCT' | 'ALL' ] selectExpression {',' selectExpression} \
                        'FROM' tableExpression {',' tableExpression} [ 'WHERE' expression ] \
                        [ 'GROUP BY' expression {',' expression} ] [ 'HAVING' expression ] \
                        [ ( 'UNION' [ 'ALL' ] | 'MINUS' | 'EXCEPT' | 'INTERSECT' ) select ] [ 'ORDER BY' order {',' order} ] \
                        [ 'LIMIT' expression [ 'OFFSET' expression ] [ 'SAMPLE_SIZE' rowCountInt ] ] \
                        [ 'FOR UPDATE' ];
                """;

        var file = new File("/tmp/railroad.svg");
        try (var istream = new ByteArrayInputStream(text.getBytes());
             var ostream = new FileOutputStream(file)) {
            handleRequest(istream, ostream, null);
        }
    }
}