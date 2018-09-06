package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.controller.helper.TemplateHelper
import br.uff.mvpcortes.prajuda.exception.PageNotFoundException
import br.uff.mvpcortes.prajuda.service.PrajDocumentService
import br.uff.mvpcortes.prajuda.service.PrajServiceService
import br.uff.mvpcortes.prajuda.service.PrajudaWorkerService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.toFlux


/**
 * This controller mapping services names on prajuda URL and return its main page or docs page
 * @see {https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-ann-requestmapping}
 */
@Controller
class PrajServiceViewController(val prajServiceService:PrajServiceService,
                                val prajudaWorkerService: PrajudaWorkerService,
                                val prajDocumentService: PrajDocumentService){

    companion object {
        const val REGEX_VALID_SERVICE_NAME = "[a-z_]+"
        const val REGEX_VALID_PATH_NAME    = "[a-z\\_]*[a-z_]+.html"
    }

    @GetMapping("{serviceName:$REGEX_VALID_SERVICE_NAME}.html")
    fun getMainPage(@PathVariable serviceName:String, model: Model):String{
        return TemplateHelper(model)
                .withAttr("services",
                        ReactiveDataDriverContextVariable(prajServiceService.findByName(serviceName).toFlux(), 1))
                .withAttr("mapHarvesterType", prajudaWorkerService.mapHarvesterTypes())
                .withPage("fragments/service/service_show").apply()
    }

    @GetMapping("{serviceName:$REGEX_VALID_SERVICE_NAME}/{docPath:$REGEX_VALID_PATH_NAME}")
    fun getDocument(@PathVariable serviceName: String, @PathVariable docPath:String, model:Model):String{

//        val prajDocument = prajDocumentService.findByServiceNameAndPath(serviceName, docPath)
//                ?: throw PageNotFoundException("$serviceName/$docPath.html")
//
//        return TemplateHelper(model)
//                .withAttr("document", prajDocument)
//                .withAttr("content_html",
//                        ReactiveDataDriverContextVariable(prajDocumentService.findByIdHtmlFlux(prajDocument.id!!)))
//                .withPage("fragments/document/document_show").apply()


        return TemplateHelper(model)
                .withAttr("documents", ReactiveDataDriverContextVariable(prajDocumentService.findByServiceAndPathFlux(serviceName, docPath)))
                .apply()
    }
}