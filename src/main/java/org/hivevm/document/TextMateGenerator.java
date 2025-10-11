// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.document;

import org.eclipse.tm4e.core.internal.grammar.ScopeStack;
import org.eclipse.tm4e.core.internal.theme.Theme;
import org.eclipse.tm4e.core.internal.theme.raw.RawThemeReader;
import org.eclipse.tm4e.core.registry.IGrammarSource;
import org.eclipse.tm4e.core.registry.IRegistryOptions;
import org.eclipse.tm4e.core.registry.IThemeSource;
import org.eclipse.tm4e.core.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextMateGenerator {

    private static final String THEME = "/themes/github-light.json";

    private static final String GRAMMAR_CPP = "/grammars/cpp.json";
    private static final String GRAMMAR_INI = "/grammars/ini.json";
    private static final String GRAMMAR_XML = "/grammars/xml.json";
    private static final String GRAMMAR_JSON = "/grammars/json.json";
    private static final String GRAMMAR_JAVA = "/grammars/java.json";
    private static final String GRAMMAR_RUST = "/grammars/rust.json";
    private static final String GRAMMAR_YAML = "/grammars/yaml.json";

    private static Theme theme;
    private static Registry registry;


    public static void generate(String type, String text, List<CodeBlock.Line> rows) throws Exception {
        if (registry == null) {
            registry = new Registry();
            registry.setTheme(IThemeSource.fromResource(TextMateGenerator.class, THEME));
            registry.addGrammar(IGrammarSource.fromResource(TextMateGenerator.class, GRAMMAR_CPP));
            registry.addGrammar(IGrammarSource.fromResource(TextMateGenerator.class, GRAMMAR_INI));
            registry.addGrammar(IGrammarSource.fromResource(TextMateGenerator.class, GRAMMAR_XML));
            registry.addGrammar(IGrammarSource.fromResource(TextMateGenerator.class, GRAMMAR_JSON));
            registry.addGrammar(IGrammarSource.fromResource(TextMateGenerator.class, GRAMMAR_JAVA));
            registry.addGrammar(IGrammarSource.fromResource(TextMateGenerator.class, GRAMMAR_RUST));
            registry.addGrammar(IGrammarSource.fromResource(TextMateGenerator.class, GRAMMAR_YAML));
        }

        if (theme == null) {
            var regOpt = new IRegistryOptions() {
            };
            theme = Theme.createFromRawTheme(
                    RawThemeReader.readTheme(IThemeSource.fromResource(TextMateGenerator.class, THEME)),
                    regOpt.getColorMap());
        }

        String color = null;
        var spans = new ArrayList<TextSpan>();

        var grammar = "xml".equalsIgnoreCase(type)
                ? registry.grammarForScopeName("text." + type)
                : registry.grammarForScopeName("source." + type);
        var lineTokens = grammar.tokenizeLine(text);
        for (var token : lineTokens.getTokens()) {
            var fg = token.getScopes().reversed().stream()
                    .map(ScopeStack::from)
                    .map(theme::match)
                    .map(s -> s.foregroundId)
                    .map(i -> theme.getColorMap().get(i))
                    .findFirst().get();

            if (color == null)
                spans.add(new TextSpan.Builder().color(fg).build());
            else if (!Objects.equals(color, fg)) {
                spans.add(new TextSpan.Builder().end().build());
                spans.add(new TextSpan.Builder().color(fg).build());
            }
            color = fg;

            var image = text.substring(token.getStartIndex(), token.getEndIndex());
            for (var elem : image.splitWithDelimiters("\\n", 0)) {
                if (elem.equals("\\n"))
                    spans.add(new TextSpan.Builder().newLine().build());
                else if (!elem.isEmpty())
                    spans.add(new TextSpan.Builder(elem).build());
            }
        }
        spans.add(new TextSpan.Builder().end().build());
        rows.add(new CodeBlock.Line(spans));
    }
}
