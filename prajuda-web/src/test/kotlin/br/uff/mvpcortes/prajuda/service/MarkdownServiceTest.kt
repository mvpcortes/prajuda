package br.uff.mvpcortes.prajuda.service

import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class MarkdownServiceTest{

    val markdownService = MarkdownService()


    @Test()
    fun `when markdown is empty then generate empty html`(){

        StepVerifier.create(
        markdownService.parseMarkdown("")
                .reduce{a,b->a+b}
        )
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown is a word then generate html with only paragraph with this word`(){

        StepVerifier.create(markdownService.parseMarkdown("xixi")
                        .reduce{a,b->a+b})
                .expectNextMatches { it.matches(Regex("\\<p\\>xixi\\<\\/p\\>\\s+"))}
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with 2 paragraphs then generate html with two paragraphs`(){

        StepVerifier.create(markdownService.parseMarkdown("My words\n\nLost in the space")
                .reduce{a,b->a+b})
                .expectNext("<p>My words</p>\n<p>Lost in the space</p>\n")
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with a title and a paragraph then generate html`(){

        StepVerifier.create(markdownService.parseMarkdown("# Title\n\nparagraph")
                .reduce{a,b->a+b})
                .expectNext("<h1>Title</h1>\n<p>paragraph</p>\n")
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with a title and a sub title and a paragraph then generate html`(){

        StepVerifier.create(markdownService.parseMarkdown("# Title\n## Subtitle\nparagraph")
                .reduce{a,b->a+b})
                .expectNext("<h1>Title</h1>\n<h2>Subtitle</h2>\n<p>paragraph</p>\n")
                .expectComplete()
                .verify()
    }

    @Test()
    fun `when markdown with a title and a sub title and a paragraph then call next in right order`(){
        StepVerifier.create(markdownService.parseMarkdown("# Title\n## Subtitle\nparagraph"))
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