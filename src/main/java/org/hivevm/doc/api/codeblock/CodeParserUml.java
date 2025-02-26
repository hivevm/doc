// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.hivevm.doc.api.builder.CodeBuilder;
import org.hivevm.doc.api.builder.CodeParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;


/**
 * The {@link CodeParserUml} class.
 */
class CodeParserUml implements CodeParser {

    /**
     * Generates the code text
     *
     * @param text
     * @param builder
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        builder.setBackground("transparent");

        String uml = "@startuml\n" + text + "\n@enduml\n";
        SourceStringReader reader = new SourceStringReader(uml);

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            reader.generateImage(bytes, new FileFormatOption(FileFormat.SVG));
            String base64 = Base64.getEncoder().encodeToString(bytes.toByteArray());
            String dataUri = "data:image/svg+xml;base64," + base64;
            builder.addImage(dataUri, null, null, null, null);
        } catch (IOException e) {
            e.printStackTrace();
            builder.addText(text);
        }
    }
}
