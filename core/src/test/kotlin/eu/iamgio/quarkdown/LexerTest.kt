@file:Suppress("ktlint:standard:no-wildcard-imports")

package eu.iamgio.quarkdown

import eu.iamgio.quarkdown.flavor.MarkdownFlavor
import eu.iamgio.quarkdown.flavor.base.BaseMarkdownFlavor
import eu.iamgio.quarkdown.flavor.quarkdown.QuarkdownFlavor
import eu.iamgio.quarkdown.lexer.*
import eu.iamgio.quarkdown.lexer.regex.StandardRegexLexer
import eu.iamgio.quarkdown.lexer.regex.pattern.TokenRegexPattern
import eu.iamgio.quarkdown.lexer.walker.SourceReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tokenization tests.
 * @see Lexer
 */
class LexerTest {
    private fun blockLexer(
        source: CharSequence,
        flavor: MarkdownFlavor = QuarkdownFlavor,
    ) = flavor.lexerFactory.newBlockLexer(source)

    @Test
    fun sourceReader() {
        val reader = SourceReader("Test")
        assertEquals('T', reader.read())
        assertEquals('e', reader.peek())
        assertEquals('e', reader.read())
        assertEquals('s', reader.read())
        assertEquals('t', reader.read())
        assertNull(reader.read())
    }

    @Test
    fun regex() {
        val wrap: (TokenData) -> Token = { ParagraphToken(it) }

        val lexer =
            StandardRegexLexer(
                "ABC\nABB\nDEF\nGHI\nDE",
                listOf(
                    TokenRegexPattern(
                        name = "FIRST",
                        wrap = wrap,
                        regex = "AB.".toRegex(),
                    ),
                    TokenRegexPattern(
                        name = "SECOND",
                        wrap = wrap,
                        regex = "DE.?".toRegex(),
                    ),
                    TokenRegexPattern(
                        name = "NEWLINE",
                        wrap = wrap,
                        regex = "\\R".toRegex(),
                    ),
                ),
                fillTokenType = wrap,
            )

        val tokens = lexer.tokenize().iterator()

        fun nextText() = tokens.next().data.text

        assertEquals("ABC", nextText())
        assertEquals("\n", nextText())
        assertEquals("ABB", nextText())
        assertEquals("\n", nextText())
        assertEquals("DEF", nextText())
        assertEquals("\n", nextText())
        assertEquals("GHI", nextText())
        assertEquals("\n", nextText())
        assertEquals("DE", nextText())
    }

    @Test
    fun blocks() {
        val tokens =
            blockLexer(readSource("/lexing/blocks.md")).tokenize().asSequence()
                .filter { it !is NewlineToken }
                .iterator()

        assertIs<HeadingToken>(tokens.next())
        assertIs<ParagraphToken>(tokens.next())
        assertIs<HeadingToken>(tokens.next())
        assertIs<ParagraphToken>(tokens.next())
        assertIs<SetextHeadingToken>(tokens.next())
        assertIs<ParagraphToken>(tokens.next())
        assertIs<UnorderedListToken>(tokens.next())
        assertIs<UnorderedListToken>(tokens.next())
        assertIs<OrderedListToken>(tokens.next())
        assertIs<BlockQuoteToken>(tokens.next())
        assertIs<BlockQuoteToken>(tokens.next())
        assertIs<BlockQuoteToken>(tokens.next())
        assertIs<BlockQuoteToken>(tokens.next())
        assertIs<BlockCodeToken>(tokens.next())
        assertIs<FencesCodeToken>(tokens.next())
        assertIs<MultilineMathToken>(tokens.next())
        assertIs<OnelineMathToken>(tokens.next())
        assertIs<HorizontalRuleToken>(tokens.next())
        assertIs<HtmlToken>(tokens.next())
        assertIs<LinkDefinitionToken>(tokens.next())
        assertIs<HorizontalRuleToken>(tokens.next())
    }

    @Test
    fun emphasis() {
        fun lex(source: CharSequence) =
            QuarkdownFlavor.lexerFactory.newInlineLexer(source.trim())
                .tokenize().asSequence()
                .filter { it !is NewlineToken }
                .iterator()

        val sources = readSource("/lexing/emphasis.md").split("\n---\n").iterator()

        repeat(2) {
            with(lex(sources.next())) {
                assertIs<StrongToken>(next())
                assertFalse(hasNext())
            }
        }

        repeat(2) {
            with(lex(sources.next())) {
                assertIs<PlainTextToken>(next())
                assertIs<StrongToken>(next())
                assertIs<PlainTextToken>(next())
                assertIs<StrongToken>(next())
                assertIs<PlainTextToken>(next())
                assertIs<EmphasisToken>(next())
                assertFalse(hasNext())
            }
        }

        with(lex(sources.next())) {
            assertIs<PlainTextToken>(next())
            assertIs<StrongToken>(next())
            assertIs<PlainTextToken>(next())
            assertIs<StrongToken>(next())
            assertIs<PlainTextToken>(next())
            assertFalse(hasNext())
        }

        with(lex(sources.next())) {
            assertIs<PlainTextToken>(next())
            assertIs<StrongToken>(next())
            assertIs<PlainTextToken>(next())
            assertFalse(hasNext())
        }

        with(lex(sources.next())) {
            assertIs<StrongToken>(next())
            assertFalse(hasNext())
        }

        with(lex(sources.next())) {
            assertIs<StrongEmphasisToken>(next())
            assertFalse(hasNext())
        }

        with(lex(sources.next())) {
            assertIs<PlainTextToken>(next())
            assertIs<StrongEmphasisToken>(next())
            assertIs<PlainTextToken>(next())
            assertFalse(hasNext())
        }

        with(lex(sources.next())) {
            assertIs<PlainTextToken>(next())
            assertFalse(hasNext())
        }

        with(lex(sources.next())) {
            assertIs<EmphasisToken>(next())
            assertIs<PlainTextToken>(next())
            assertFalse(hasNext())
        }

        with(lex(sources.next())) {
            assertIs<PlainTextToken>(next())
            assertIs<StrongToken>(next())
            assertIs<PlainTextToken>(next())
            assertFalse(hasNext())
        }
    }

    @Test
    fun flavors() {
        // Quarkdown features are not detected when using BaseMarkdownFlavor
        val tokens = blockLexer(readSource("/lexing/blocks.md"), flavor = BaseMarkdownFlavor).tokenize()
        assertTrue(tokens.filterIsInstance<MultilineMathToken>().isEmpty())
        assertTrue(tokens.filterIsInstance<OnelineMathToken>().isEmpty())
        assertFalse(tokens.filterIsInstance<BlockQuoteToken>().isEmpty())
    }
}
