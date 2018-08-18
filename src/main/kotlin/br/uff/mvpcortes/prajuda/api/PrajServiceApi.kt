package br.uff.mvpcortes.prajuda.api

import br.uff.mvpcortes.prajuda.controller.helper.TemplateRedirect
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.service.HarvesterService
import br.uff.mvpcortes.prajuda.service.PrajServiceService
import org.apache.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.EntityResponse
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController()
@RequestMapping("service")
class PrajServiceApi(private val harvesterService: HarvesterService, private val prajServiceService: PrajServiceService) {

    @PostMapping(value=[""])
    fun save(@Valid prajService: PrajService, bindingResult: BindingResult): Mono<EntityResponse<Object>> {
        if(bindingResult.hasErrors()){
            //https://stackoverflow.com/a/10049138/8313595
            return EntityResponse.fromObject(bindingResult as Object).status(HttpStatus.SC_CONFLICT).build()
        }

        Thread.sleep(10000)
        return prajServiceService.save(prajService)
                .let{EntityResponse.fromObject(it as Object).build()}
    }
}