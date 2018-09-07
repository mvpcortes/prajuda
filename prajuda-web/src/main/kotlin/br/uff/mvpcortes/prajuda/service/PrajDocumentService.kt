package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajDocument
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux

@Service
class PrajDocumentService(
        val prajudaServiceService: PrajServiceService,
        val markdownService: MarkdownService,
        val prajDocumentDAO: PrajDocumentDAO
) {

    val logger = loggerFor(PrajDocumentService::class)

    fun findByServiceNameAndPath(serviceName:String, path:String): PrajDocument?{
        return prajDocumentDAO.findByServiceNamePathAndPath(serviceName, path)
    }

//    fun findByIdHtmlFlux(documentId:String): Flux<String> {
//
//        return prajDocumentDAO.findDocById(documentId)
//                .reduce { a, b-> a + b }//concat string because we markdown framework need process a unique string
//                .map{ markdownService.parseMarkdown(it) }
//                .toFlux()
//                .flatMap{it}
//    }

    inner class PrajDocumentHtml(val prajDocument: PrajDocument,  val html:String = markdownService.parseMarkdown(prajDocument.content)){
        val id:String get()=prajDocument.id!!
        val tag:String get()=prajDocument.tag
        val path:String get()=prajDocument.path
        val serviceName:String get()=prajDocument.serviceName?:"n/a"

    }

    fun findByServiceAndPathFlux(serviceName: String, docPath: String): Flux<PrajDocumentHtml> =
        Flux.generate<PrajDocument> { sink->
            prajDocumentDAO.findByServiceNamePathAndPath(serviceName, docPath)
                    ?.let{
                        sink.next(it)
                    }
            sink.complete()
        }
        .map{PrajDocumentHtml(it)}
}