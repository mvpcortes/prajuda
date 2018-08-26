package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajService
import reactor.core.publisher.Flux

/**
 * Implement a FluxHarvesterProcessor then wrap and use a simple HarvesterProcessor backend
 */
open class SimpleFluxHarvesterProcessor(private val harvesterProcessor:HarvesterProcessor):FluxHarvesterProcessor {

    override fun harvestFlux(service: PrajService): Flux<Harvested>{
        return Flux.create<Harvested> { sink->
            harvesterProcessor.harvest(service, { sink.next(it)})
            sink.complete()
        }
    }

    override fun harvestCompleteFlux(service: PrajService): Flux<Harvested>{
        return Flux.create<Harvested> { sink->
            harvesterProcessor.harvestComplete(service, { sink.next(it)})
            sink.complete()
        }
    }

}