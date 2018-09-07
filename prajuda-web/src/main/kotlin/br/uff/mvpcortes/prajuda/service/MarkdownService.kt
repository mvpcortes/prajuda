package br.uff.mvpcortes.prajuda.service

import com.vladsch.flexmark.ast.Document
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.options.MutableDataSet
import org.springframework.stereotype.Service
import reactor.core.publisher.*
import java.util.Arrays.asList


@Service
class MarkdownService{


    /**
     * Used to fill the flux
     */
    class AppendableForFlux(val sink: FluxSink<String>):Appendable{
        override fun append(sequence: CharSequence): java.lang.Appendable {
            sink.next(sequence.toString())
            return this
        }

        override fun append(sequence: CharSequence, init: Int, end: Int): java.lang.Appendable {
            sink.next(sequence.substring(init, end))
            return this
        }

        override fun append(sequence: Char): java.lang.Appendable {
            sink.next(sequence.toString())
            return this
        }
    }

    fun parseMarkdown(strMarkdown: String):String{
        val options = MutableDataSet()
        options.setFrom(ParserEmulationProfile.COMMONMARK_0_28)
        options.set(HtmlRenderer.GENERATE_HEADER_ID, false)
        options.set(Parser.EXTENSIONS, asList(

        ))

        val parser = Parser.builder(options).build()

        val document = parser.parse(strMarkdown)
        val stringBuilder = StringBuilder()

        val renderer = HtmlRenderer.builder(options).build()
        renderer.render(document, stringBuilder, 1_000)
        return stringBuilder.toString()
    }

    /**
     * Parsing markdown to html flux
     */
    fun parseMarkdownFlux(strMarkdown:String): Flux<String> {
        val options = MutableDataSet()
        options.setFrom(ParserEmulationProfile.COMMONMARK_0_28)
        options.set(HtmlRenderer.GENERATE_HEADER_ID, false)
        options.set(Parser.EXTENSIONS, asList(
//                HtmlRenderer.AUTOLINK_WWW_PREFIX
//                TablesExtension.create()

        ))

        val parser = Parser.builder(options).build()

        val monoDoc : Mono<Document> = { parser.parse(strMarkdown) }.toMono()


        return monoDoc
                .toFlux()
                .map{ Pair(it, HtmlRenderer.builder(options).build()) } //create renderer
                .flatMap{ itDoc->
                    Flux.create<String>{
                        val appendable = AppendableForFlux(it)
                        try {
                            itDoc.second.render(itDoc.first, appendable, 1_000)
                            it.complete()
                        }catch(e:Throwable){
                            it.error(e)
                        }
                    }
                }
    }
}