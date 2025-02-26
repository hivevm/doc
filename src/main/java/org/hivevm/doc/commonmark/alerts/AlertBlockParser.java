// Copyright 2025 HiveVM.org. All rights reserved.
// SPDX-License-Identifier: BSD-3-Clause

package org.hivevm.doc.commonmark.alerts;

import org.commonmark.node.Block;
import org.commonmark.parser.block.*;

import java.util.regex.Matcher;

public class AlertBlockParser extends AbstractBlockParser {

    private final AlertBlock block;
    private       boolean    multiline;

    public AlertBlockParser(Alert type, boolean multiline) {
        this.block = new AlertBlock(type);
        this.multiline = multiline;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public boolean canContain(Block block) {
        return (block != null) && !AlertBlock.class.isAssignableFrom(block.getClass());
    }

    @Override
    public AlertBlock getBlock() {
        return this.block;
    }

    @Override
    public BlockContinue tryContinue(ParserState state) {
        CharSequence content = state.getLine().getContent();
        CharSequence line = content.subSequence(state.getColumn() + state.getIndent(),
                content.length());

        if (!this.multiline) {
            return BlockContinue.finished();
        }

        if ((line.length() > 1) && (line.charAt(0) == '!') && (line.charAt(1) == '!')) {
            this.multiline = false;
            return BlockContinue.finished();
        }

        return BlockContinue.atColumn(state.getColumn() + state.getIndent());
    }

    /**
     * The {@link Factory} class to create an {@link AlertBlockParser}.
     */
    public static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            CharSequence content = state.getLine().getContent();
            CharSequence line = content.subSequence(state.getColumn(), content.length());

            Matcher matcher = Alert.matcher(line);
            if (matcher.matches()) {
                boolean isMultiline = "!".equals(matcher.group(2));
                AlertBlockParser parser = new AlertBlockParser(Alert.of(matcher.group(3)), isMultiline);
                return BlockStart.of(parser)
                        .atColumn(state.getColumn() + state.getIndent() + matcher.end(1));
            }
            return BlockStart.none();
        }
    }
}
