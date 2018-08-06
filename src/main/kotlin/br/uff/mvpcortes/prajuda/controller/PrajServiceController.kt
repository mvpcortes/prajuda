package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.controller.helper.MessageError
import br.uff.mvpcortes.prajuda.controller.helper.PageRequest
import br.uff.mvpcortes.prajuda.controller.helper.TemplateHelper
import br.uff.mvpcortes.prajuda.controller.helper.TemplateRedirect
import br.uff.mvpcortes.prajuda.controller.helper.pagination.Pagination
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.service.HarvesterService
import br.uff.mvpcortes.prajuda.service.PrajServiceService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import javax.validation.Valid
import kotlin.math.min

@Controller()
@RequestMapping("service")
class PrajServiceController(private val harvesterService: HarvesterService, private val prajServiceService: PrajServiceService) {

    private val listServices:List<PrajService> = (1..500).map{PrajService(it.toString(), "Service $it")}.toList()

    @ModelAttribute("harvesterTypes")
    fun harvesterTypes() = harvesterService.harvesterTypes

    @GetMapping(value = ["index.html"])
    fun list(pageRequest: PageRequest = PageRequest(), model: Model): String {
        val services    = getServices(pageRequest.page, pageRequest.pageSize)
        val qtdService = getQtdServices()
        val pagination = Pagination(qtdService/pageRequest.pageSize, pageRequest.page)

        model.addAttribute("services", ReactiveDataDriverContextVariable(services))
        model.addAttribute("pagination", pagination)
        model.addAttribute("qtd_services", qtdService)
        model.addAttribute("pageRequest", pageRequest)


        return TemplateHelper(model).withPage("fragments/service/list_service").apply()
    }

    @GetMapping(value=["new.html"])
    fun new(@RequestParam(name=TemplateRedirect.STR_ERROR_ATTR, required = false) messageError: MessageError?,
            @RequestParam(name="service", required = false) prajService:PrajService?,
            model:Model):String{


        return TemplateHelper(model)
                .withAttr("service", prajService?:PrajService.empty())
                .withAttrNotNull(TemplateRedirect.STR_ERROR_ATTR, messageError)
                .withPage("fragments/service/new_service")
                .apply()
    }

    @PostMapping(value=[""])
    fun save(@Valid prajService:PrajService, bindingResult: BindingResult, model:Model):String{
        if(bindingResult.hasErrors()){
            //https://stackoverflow.com/a/10049138/8313595
            return TemplateRedirect("service/new.html").withError(bindingResult)
                    .addParam("prajService", prajService)
                    .apply()
        }

        prajService.takeIf{it.id?.isBlank()==true}?.let{it.id=null}
        return prajServiceService.save(prajService).let{"redirect:/service/${it.id}.html"}
    }

    private fun getQtdServices(): Int {
        return listServices.size
    }

    private fun getServices(page: Int, pageSize: Int): Flux<PrajService> {
        return listServices.subList((page-1)*pageSize, min((page)*pageSize, listServices.size)).toFlux()
    }
}