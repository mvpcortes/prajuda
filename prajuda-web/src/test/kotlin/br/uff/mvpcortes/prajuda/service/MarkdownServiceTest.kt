package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.model.fixture.PrajDocumentFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class MarkdownServiceTest{

    val markdownService = MarkdownService()

    val STR_MD_1 = "xixi"
    val STR_MD_2 = "My words\n\nLost in the space"
    val STR_MD_3 = "# Title\n\nparagraph"
    val STR_MD_4 = PrajDocumentFixture.STR_MD_SIMPLE

    val REGEX_VALID_MD_1 = "\\<p\\>xixi\\<\\/p\\>\\s+"
    val STR_VALID_MD_2 = "<p>My words</p>\n<p>Lost in the space</p>\n"
    val STR_VALID_MD_3 = "<h1>Title</h1>\n<p>paragraph</p>\n"
    val STR_VALID_MD_4 = PrajDocumentFixture.STR_VALID_MD_SIMPLE




    @Nested
    inner class `when using simple parse`(){

        @Test
        fun `markdown 1 valid should be renderized`(){
            assertThat(markdownService.parseMarkdown(STR_MD_1)).matches(REGEX_VALID_MD_1)
        }

        @Test
        fun `markdown 2 valid should be renderized`(){
            assertThat(markdownService.parseMarkdown(STR_MD_2)).isEqualTo(STR_VALID_MD_2)
        }

        @Test
        fun `markdown 3 valid should be renderized`(){
            assertThat(markdownService.parseMarkdown(STR_MD_3)).isEqualTo(STR_VALID_MD_3)
        }

        @Test
        fun `markdown 4 valid should be renderized`(){
            assertThat(markdownService.parseMarkdown(STR_MD_4)).isEqualTo(STR_VALID_MD_4)
        }
    }


    @Test()
    fun `when markdown is empty then generate empty html`(){

        StepVerifier.create(
        markdownService.parseMarkdownFlux("")
                .reduce{a,b->a+b}
        )
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown is a word then generate html with only paragraph with this word`(){

        StepVerifier.create(markdownService.parseMarkdownFlux(STR_MD_1)
                        .reduce{a,b->a+b})
                .expectNextMatches {
                    it.matches(Regex(REGEX_VALID_MD_1))}
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with 2 paragraphs then generate html with two paragraphs`(){

        StepVerifier.create(markdownService.parseMarkdownFlux(STR_MD_2)
                .reduce{a,b->a+b})
                .expectNext(STR_VALID_MD_2)
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with a title and a paragraph then generate html`(){

        StepVerifier.create(markdownService.parseMarkdownFlux(STR_MD_3)
                .reduce{a,b->a+b})
                .expectNext(STR_VALID_MD_3)
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with a title and a sub title and a paragraph then generate html`(){

        StepVerifier.create(markdownService.parseMarkdownFlux(STR_MD_4)
                .reduce{a,b->a+b})
                .expectNext(STR_VALID_MD_4)
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with a title and a sub title and a paragraph then call next in right order`(){
        StepVerifier.create(markdownService.parseMarkdownFlux("# Title\n## Subtitle\nparagraph"))
                .expectNext( "<", "h1", ">")
                .expectNext("Title")
                .expectNext("</", "h1", ">")
                .expectNext("\n")
                .expectNext("<", "h2", ">", "Subtitle", "</", "h2", ">")
                .expectNext( "\n")
                .expectNext( "<", "p", ">", "paragraph", "</", "p", ">")
                .expectNext( "\n")
                .expectComplete()
                .verify()
    }
}