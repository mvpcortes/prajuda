package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
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

    fun findByServiceNameAndPath(serviceName:String, path:String): PrajDocument?{
        return prajDocumentDAO.findByServiceNamePathAndPath(serviceName, path)
    }

    fun findByIdHtmlFlux(documentId:String): Flux<String> {

        return prajDocumentDAO.findDocById(documentId)
                .reduce { a, b-> a + b }//concat string because we markdown framework need process a unique string
                .map{ markdownService.parseMarkdown(it) }
                .toFlux()
                .flatMap{it}
    }

    fun findByServiceAndPathFlux(serviceName: String, docPath: String): Flux<PrajDocument> {
        return Flux.generate { sink->
            prajDocumentDAO.findByServiceNamePathAndPath(serviceName, docPath)
                    ?.let{ sink.next(it) }

            sink.complete()
        }
    }
}