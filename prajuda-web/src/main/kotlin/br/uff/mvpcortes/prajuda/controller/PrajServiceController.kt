package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.controller.helper.MessageError
import br.uff.mvpcortes.prajuda.controller.helper.PageRequest
import br.uff.mvpcortes.prajuda.controller.helper.TemplateHelper
import br.uff.mvpcortes.prajuda.controller.helper.TemplateRedirect
import br.uff.mvpcortes.prajuda.controller.helper.pagination.Pagination
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.service.PrajServiceService
import br.uff.mvpcortes.prajuda.service.PrajudaWorkerService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.util.UriComponentsBuilder
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux

@Controller()
@RequestMapping("service")
class PrajServiceController(private val prajudaWorkerService: PrajudaWorkerService, private val prajServiceService: PrajServiceService) {

    @ModelAttribute("harvesterTypes")
    fun harvesterTypes() = prajudaWorkerService.harvesterTypes()

    @GetMapping(value=["{id}.html"])
    fun get(@PathVariable id:String, model:Model): String {
        return TemplateHelper(model)
                .withAttr("service", prajServiceService.findById(id) as Any)
                .withAttr("mapHarvesterType", prajudaWorkerService.mapHarvesterTypes())
                .withPage("fragments/service/service_show").apply()
    }

    @GetMapping(value=["{id}/edit.html"])
    fun edit(@PathVariable id:String, model:Model): String {
        return TemplateHelper(model)
                .withAttr("service", prajServiceService.findById(id)!!)
                .withAttr("mapHarvesterType", prajudaWorkerService.mapHarvesterTypes())
                .withPage("fragments/service/service_new").apply()
    }

    @GetMapping(value = ["index.html"])
    fun list(pageRequest: PageRequest = PageRequest(), model: Model): String {
        val services    = getServices(pageRequest.page-1, pageRequest.pageSize)
        val qtdService = prajServiceService.count().toInt()
        val pagination = Pagination(qtdService/pageRequest.pageSize, pageRequest.page)

        model.addAttribute("services", ReactiveDataDriverContextVariable(services))
        model.addAttribute("pagination", pagination)
        model.addAttribute("qtd_services", qtdService)
        model.addAttribute("pageRequest", pageRequest)


        return TemplateHelper(model).withPage("fragments/service/service_list").apply()
    }

    @GetMapping(value=["new.html"])
    fun new(@RequestParam(name=TemplateRedirect.STR_ERROR_ATTR, required = false) messageError: MessageError?,
            @RequestParam(name="service", required = false) prajService:PrajService?,
            model:Model):String{

        return TemplateHelper(model)
                .withAttr("service", prajService?:PrajService.empty())
                .withAttrNotNull(TemplateRedirect.STR_ERROR_ATTR, messageError)
                .withPage("fragments/service/service_new")
                .apply()
    }



    private fun getServices(page: Int, pageSize: Int): Flux<PrajService> {
        return prajServiceService.findServices(page, pageSize)//listServices.subList((page-1)*pageSize, min((page)*pageSize, listServices.size)).toFlux()
    }
}