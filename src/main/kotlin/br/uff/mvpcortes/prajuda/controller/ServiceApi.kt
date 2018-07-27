package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController()
@RequestMapping("service")
class ServiceController (val prajServiceDAO: PrajServiceDAO){


    /**
     * @see [https://stackoverflow.com/a/45759306/8313595]
     */
    @GetMapping("{id}")
    fun get(@PathVariable id:String): Mono<PrajService>{
        return prajServiceDAO.findMonoById(id)
//                .map { ResponseEntity.ok(it) }
//                .defaultIfEmpty(notFound().build())

//        val m:Mono<ResponseEntity<PrajService>> = Mono.empty()
//        return m.defaultIfEmpty(notFound().build())

    }

    @PostMapping()
    fun post(prajService: PrajService){

    }
}