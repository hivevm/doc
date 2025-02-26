// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document.code;

import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

/**
 * The {@link DiagramUmlTest} class.
 */
public class DiagramUmlTest extends DiagramTest {

    private record Diagram(String text, String type) {

        public Diagram(String text) {
            this(text, "uml");
        }
    }

    protected final void handleRequest(InputStream istream, OutputStream ostream, String type) throws IOException {
        Graphviz.useEngine(new GraphvizV8Engine());
        var text = new String(istream.readAllBytes());
        var uml = "@start" + type + "\n!pragma layout smetana\n" + text + "\n@end\" + type + \"\n";
        var reader = new SourceStringReader(uml);
        reader.outputImage(ostream, new FileFormatOption(FileFormat.SVG));
    }

    private static final Diagram[] DIAGRAMS = {
            new Diagram("""
                    interface DocumentElement
                    interface ParagraphElement
                    interface FlowElement
                    
                    DocumentElement <|.. Header
                    DocumentElement <|.. Break
                    DocumentElement <|.. Table
                    DocumentElement <|.. ParagraphElement
                    ParagraphElement <|.. Paragraph
                    ParagraphElement <|.. BlockQuotes
                    ParagraphElement <|.. List
                    ParagraphElement <|.. CodeBlock
                    
                    Document "1" *--> "*" DocumentElement : contains
                    Header "1" *--> "1" FlowElement : contains
                    Paragraph "1" *--> "1" FlowElement : contains
                    CodeBlock "1" *--> "*" FlowElement : contains
                    """),
            new Diagram("""
                    interface FlowElement
                    FlowElement <|.. Text
                    FlowElement <|.. Link
                    FlowElement <|.. Style
                    FlowElement <|.. Image
                    FlowElement "1" *--> "*" FlowElement : contains
                    """), new Diagram(
            """
                    class Database {}
                    class ObjectCollection {}
                    class ObjectReference {}
                    class PersistentObject {}
                    class ObjectInfo {}
                    
                    hide members
                    
                    Database   -d-> ObjectCollection : supplies
                    ObjectCollection -d-> PersistentObject         : manages persistence
                    ObjectReference  -u-> ObjectCollection         : uses
                    PersistentObject "1" -u-> "*" ObjectCollection : contains
                    PersistentObject "1" -l-> "*" ObjectReference  : contains
                    ObjectInfo       -u-> PersistentObject         : describes
                    """), new Diagram(
            """
                    start
                    repeat
                      :Test something;
                        if (Something went wrong?) then (no)
                          #palegreen:OK;
                          break
                        endif
                        ->NOK;
                        :Alert "Error with long text";
                    repeat while (Something went wrong with long text?) is (yes) not (no)
                    ->//merged step//;
                    :Alert "Success";
                    stop
                    """,
            """
                    participant Participant as Foo
                    actor       Actor       as Foo1
                    boundary    Boundary    as Foo2
                    control     Control     as Foo3
                    entity      Entity      as Foo4
                    database    Database    as Foo5
                    collections Collections as Foo6
                    queue       Queue       as Foo7
                    Foo -> Foo1 : To actor
                    Foo -> Foo2 : To boundary
                    Foo -> Foo3 : To control
                    Foo -> Foo4 : To entity
                    Foo -> Foo5 : To database
                    Foo -> Foo6 : To collections
                    Foo -> Foo7: To queue
                    """),
            new Diagram("""
                    <style>
                      .h1 {
                        BackGroundColor green
                        FontColor white
                        FontStyle italic
                      }
                      .h2 {
                        BackGroundColor red
                        FontColor white
                        FontStyle bold
                      }
                    </style>
                    #highlight "lastName"
                    #highlight "address" / "city" <<h1>>
                    #highlight "phoneNumbers" / "0" / "number" <<h2>>
                    {
                      "firstName": "John",
                      "lastName": "Smith",
                      "isAlive": true,
                      "age": 28,
                      "address": {
                        "streetAddress": "21 2nd Street",
                        "city": "New York",
                        "state": "NY",
                        "postalCode": "10021-3100"
                      },
                      "phoneNumbers": [
                        {
                          "type": "home",
                          "number": "212 555-1234"
                        },
                        {
                          "type": "office",
                          "number": "646 555-4567"
                        }
                      ],
                      "children": [],
                      "spouse": null
                    }
                    """, "json"),
            new Diagram("""
                    {
                      "firstName": "John",
                      "lastName": "Smith",
                      "isAlive": true,
                      "age": 27,
                      "address": {
                        "streetAddress": "21 2nd Street",
                        "city": "New York",
                        "state": "NY",
                        "postalCode": "10021-3100"
                      },
                      "phoneNumbers": [
                        {
                          "type": "home",
                          "number": "212 555-1234"
                        },
                        {
                          "type": "office",
                          "number": "646 555-4567"
                        }
                      ],
                      "children": [],
                      "spouse": null
                    }
                    """, "json"),
            new Diagram("""
                    title LISP Grammar
                    grammars_expression = atomic_symbol | "(", s_expression, ".", s_expression, ")" | list;
                    list = "(", s_expression, { s_expression }, ")";
                    atomic_symbol = letter, atom_part;
                    atom_part = empty | letter, atom_part | number, atom_part;
                    letter = ? a-z ?;
                    number = ? 1-9 ?;
                    empty = " ";
                    """, "ebnf"),
            new Diagram("""
                    !option useDescriptiveNames false
                    
                    \\d?\\D+\\w*\\W{1,2}|\\s.\\S
                    """, "regex"),
            new Diagram("""
                    title Semantic Versioning 2.0
                    semver = version_core "-", pre_release "+", build;
                    """, "ebnf")
    };

    @TestFactory
    Stream<DynamicTest> dynamicDiagramsTestsFromIterator() {
        return Arrays.stream(DIAGRAMS).map(diagram -> DynamicTest.dynamicTest("1st dynamic test", () -> {
                    var path = "/tmp/diagram" + new Random().nextInt() + ".svg";
                    var bytes = diagram.text().getBytes();
                    try (var istream = new ByteArrayInputStream(bytes);
                         var ostream = new FileOutputStream(path)) {
                        handleRequest(istream, ostream, diagram.type());
                    }
                })
        );
    }
}