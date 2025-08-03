package com.quarkdown.core.flavor.base

import com.quarkdown.core.flavor.LexerFactory
import com.quarkdown.core.lexer.Lexer
import com.quarkdown.core.lexer.patterns.BaseMarkdownBlockTokenRegexPatterns
import com.quarkdown.core.lexer.patterns.BaseMarkdownInlineTokenRegexPatterns
import com.quarkdown.core.lexer.regex.StandardRegexLexer
import com.quarkdown.core.lexer.tokens.PlainTextToken

/**
 * [BaseMarkdownFlavor] lexer factory.
 */
object BaseMarkdownLexerFactory : LexerFactory {
    private val blockPatterns = BaseMarkdownBlockTokenRegexPatterns()
    private val inlinePatterns = BaseMarkdownInlineTokenRegexPatterns()

    override fun newBlockLexer(source: CharSequence): StandardRegexLexer =
        with(blockPatterns) {
            StandardRegexLexer(
                source,
                listOf(
                    comment,
                    blockQuote,
                    blockCode,
                    footnoteDefinition,
                    linkDefinition,
                    fencesCode,
                    heading,
                    horizontalRule,
                    setextHeading,
                    table,
                    unorderedList,
                    orderedList,
                    newline,
                    paragraph,
                    blockText,
                ),
            )
        }

    override fun newListLexer(source: CharSequence): StandardRegexLexer =
        with(blockPatterns) {
            StandardRegexLexer(
                source,
                listOf(listItem, newline),
            )
        }

    override fun newInlineLexer(source: CharSequence): StandardRegexLexer =
        newLinkLabelInlineLexer(source).updatePatterns { patterns ->
            with(inlinePatterns) {
                listOf(
                    diamondAutolink,
                    link,
                    referenceFootnote,
                    referenceLink,
                    urlAutolink,
                ) + patterns
            }
        }

    override fun newLinkLabelInlineLexer(source: CharSequence): StandardRegexLexer =
        with(inlinePatterns) {
            StandardRegexLexer(
                source,
                listOf(
                    lineBreak,
                    codeSpan,
                    escape,
                    entity,
                    comment,
                    image,
                    referenceImage,
                    strongEmphasisAsterisk,
                    strongEmphasisUnderscore,
                    emphasisAsterisk,
                    emphasisUnderscore,
                    strongAsterisk,
                    strongUnderscore,
                    strikethrough,
                    criticalContent,
                ),
                fillTokenType = ::PlainTextToken,
            )
        }

    // Functions aren't supported by this flavor
    override fun newExpressionLexer(
        source: CharSequence,
        allowBlockFunctionCalls: Boolean,
    ): Lexer = StandardRegexLexer(source, patterns = emptyList())
}
