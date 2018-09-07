package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.controller.helper.TemplateHelper
import br.uff.mvpcortes.prajuda.exception.BadRequestException
import br.uff.mvpcortes.prajuda.exception.PageNotFoundException
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.validation.RelativePath
import br.uff.mvpcortes.prajuda.service.PrajDocumentService
import br.uff.mvpcortes.prajuda.service.PrajServiceService
import br.uff.mvpcortes.prajuda.service.PrajudaWorkerService
import org.springframework.boot.context.properties.bind.BindResult
import org.springframework.format.Formatter
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.PathVariable
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.toFlux
import java.beans.PropertyEditorSupport
import java.util.*
import java.util.stream.Collectors
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import org.springframework.validation.BeanPropertyBindingResult
import reactor.core.publisher.onErrorReturn


/**
 * This controller mapping services names on prajuda URL and return its main page or docs page
 * @see {https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-ann-requestmapping}
 */
@Controller
class PrajServiceViewController(val prajServiceService:PrajServiceService,
                                val prajudaWorkerService: PrajudaWorkerService,
                                val prajDocumentService: PrajDocumentService,
                                val springValidator: SpringValidatorAdapter){

    companion object {
        const val REGEX_VALID_SERVICE_NAME = "[\\w\\d_]+"
    }

    @GetMapping("{serviceName:$REGEX_VALID_SERVICE_NAME}.html")
    fun getMainPage(@PathVariable serviceName:String, model: Model):String{
        return TemplateHelper(model)
                .withAttr("services",
                        ReactiveDataDriverContextVariable(prajServiceService.findByName(serviceName).toFlux(), 1))
                .withAttr("mapHarvesterType", prajudaWorkerService.mapHarvesterTypes())
                .withPage("fragments/service/service_show").apply()
    }


    class DocumentPathFormatter: Formatter<DocumentPath>{
        override fun parse(path: String, locale: Locale) = DocumentPath(path)

        override fun print(obj: DocumentPath, locale: Locale)= obj.toString()

    }

    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        binder.addCustomFormatter(DocumentPathFormatter())
    }


    class DocumentPath(path:String){
        val splited = removeFirstPath(path).split("/")

        private fun removeFirstPath(path: String): String {
            return if(path.isNotEmpty() && path[0] == '/'){
                path.substring(1)
            }else{
                path
            }
        }

        @field:Pattern(
                regexp= PrajService.REGEX_PATH_NAME_VALIDATION,
                message = "{br.uff.mvpcortes.prajuda.model.PrajService.namePath.message}")
        @field:NotNull
        val serviceName = if(splited.size<=1) {""} else { splited[0] } //if has <=2 elements it should not have service.

        @field:RelativePath val path = splited.stream()
                .skip(if(splited.size<=1){0}else{1})
                .collect(Collectors.joining("/"))
                .substringBeforeLast(".")

        @field:Pattern(regexp = "(html)|()")
        val extension = splited.last().let {
            it.takeIf { it.contains(".") }
                    ?.let { it.substringAfterLast(".") }
                    ?: ""
        }

        override fun toString(): String {
            return "$serviceName/$path.$extension"
        }
    }

    @GetMapping("document/{*documentPath}")
    fun getDocument(@PathVariable documentPath: DocumentPath,
                    model:Model):String{

        verifyDocumentPath(documentPath)

        return TemplateHelper(model)
                .withAttr("documents", ReactiveDataDriverContextVariable(
                        prajDocumentService.findByServiceAndPathFlux(documentPath.serviceName, documentPath.path)
                                .onErrorMap { e-> PageNotFoundException("document/${documentPath.path}", e) }
                ))
                .withPage("fragments/document/document_show")
                .apply()

        //        val prajDocument = prajDocumentService.findByServiceNameAndPath(serviceName, docPath)
//                ?: throw PageNotFoundException("$serviceName/$docPath.html")
//
//        return TemplateHelper(model)
//                .withAttr("document", prajDocument)
//                .withAttr("content_html",
//                        ReactiveDataDriverContextVariable(prajDocumentService.findByIdHtmlFlux(prajDocument.id!!)))
//                .withPage("fragments/document/document_show").apply()


    }

    fun verifyDocumentPath(documentPath: DocumentPath) {
        val bindingResult = BeanPropertyBindingResult(documentPath, "documentPath")
        springValidator.validate(documentPath, bindingResult)

        if (bindingResult.hasErrors()) {
            throw BadRequestException("Invalid document path")
        }
    }

}