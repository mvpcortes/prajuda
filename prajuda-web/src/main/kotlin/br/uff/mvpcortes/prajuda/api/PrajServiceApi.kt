package br.uff.mvpcortes.prajuda.api

import br.uff.mvpcortes.prajuda.api.dto.responseErrorOr
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.WithId
import br.uff.mvpcortes.prajuda.service.HarvesterService
import br.uff.mvpcortes.prajuda.service.PrajServiceService
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController()
@RequestMapping("service")
class PrajServiceApi(private val harvesterService: HarvesterService, private val prajServiceService: PrajServiceService) {

    @PostMapping(value=[""])
    fun  save(@Valid prajService: PrajService, bindingResult: BindingResult): Mono<ResponseEntity<WithId>> =
            bindingResult.responseErrorOr { ResponseEntity.ok().body(prajServiceService.save(prajService).onlyId()) }
}