// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.api.codeblock;

import org.hivevm.doc.api.builder.CodeBuilder;
import org.hivevm.doc.api.builder.ContainerBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The {@link CodeFactory} implements a tokenizer for a specific language.
 */
public class CodeFactory implements CodeParser {

    private final Map<String, CodeParser> parsers = new HashMap<>();

    /**
     * Constructs an instance of {@link CodeFactory}.
     */
    public CodeFactory() {
        this.parsers.put("conf", new CodeParserConf());
        this.parsers.put("shell", new CodeParserShell());

        this.parsers.put("meta", new CodeParserMeta());
        this.parsers.put("java", new CodeParserJava());
        this.parsers.put("c", new CodeParserCpp());
        this.parsers.put("cpp", new CodeParserCpp());
        this.parsers.put("c++", new CodeParserCpp());
        this.parsers.put("sql", new CodeParserSql());
        this.parsers.put("oql", new CodeParserSql());

        this.parsers.put("xml", new CodeParserXml());
        this.parsers.put("json", new CodeParserJson());
        this.parsers.put("api", new CodeParserApi());
    }

    /**
     * Generates the code text
     */
    @Override
    public final void generate(String text, CodeBuilder builder) {
        for (var line : text.split("\n"))
            builder.getFlowBuilder().addText(line.isEmpty() ? " " : line);
    }

    /**
     * Creates a {@link CodeFactory} for a specific language.
     */
    public final void generate(String name, String text, ContainerBuilder builder) {
        Properties properties = new Properties();
        String key = name.toLowerCase();
        if (key.contains("{")) {
            String options = key.substring(key.indexOf('{') + 1, key.length() - 1);
            for (String option : options.split(" ")) {
                var keyValue = option.split("=");
                properties.put(keyValue[0], keyValue[1]);
            }
            key = key.substring(0, key.indexOf('{'));
        }

        CodeParser parser = this.parsers.getOrDefault(key, this);
        parser.generate(text, builder, properties);
    }
}
