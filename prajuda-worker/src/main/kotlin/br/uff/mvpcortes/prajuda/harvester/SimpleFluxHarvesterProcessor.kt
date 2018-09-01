package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import reactor.core.publisher.Flux

/**
 * Implement a FluxHarvesterProcessor then wrap and use a simple HarvesterProcessor backend
 */
open class SimpleFluxHarvesterProcessor(private val harvesterProcessor:HarvesterProcessor):FluxHarvesterProcessor {

    override fun harvestFlux(service: PrajService): Flux<Harvested>{
        return Flux.create<Harvested> { sink->
            try {
                harvesterProcessor.harvest(service) { sink.next(it) }
            }catch(e:Exception){
                sink.next(Harvested(HarvestedOp.FAIL, PrajDocument(serviceId = service.id), e))
            }finally {
                sink.complete()
            }
        }
    }

    override fun harvestCompleteFlux(service: PrajService): Flux<Harvested>{
        return Flux.create<Harvested> { sink->
            try {
                harvesterProcessor.harvestComplete(service) { sink.next(it) }
            }catch(e:Exception) {
                sink.next(Harvested(HarvestedOp.FAIL, PrajDocument(serviceId = service.id), e))
            }finally {
                sink.complete()
            }
        }
    }

}