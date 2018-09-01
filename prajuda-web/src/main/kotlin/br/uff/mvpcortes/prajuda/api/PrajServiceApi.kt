package br.uff.mvpcortes.prajuda.api

import br.uff.mvpcortes.prajuda.api.dto.toMonoResponseEntityErrorMessages
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.WithId
import br.uff.mvpcortes.prajuda.service.PrajServiceService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

@RestController()
@RequestMapping("service")
class PrajServiceApi( private val prajServiceService: PrajServiceService) {


    @PostMapping()
    fun save(@Validated @RequestBody monoPrajService:Mono<PrajService>):Mono<ResponseEntity<WithId>>{
        return monoPrajService
                .map{ prajServiceService.save(it).onlyId() }
                .map{ ResponseEntity.ok(it)}
                .onErrorResume({ ex-> ex is WebExchangeBindException}, {
                    (it as WebExchangeBindException).bindingResult.toMonoResponseEntityErrorMessages<WithId>()
                })
    }
}