package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.HarvestType
import br.uff.mvpcortes.prajuda.model.PrajService
import reactor.core.publisher.Flux

interface FluxHarvesterProcessor {
    fun harvestFlux(service: PrajService): Flux<Harvested>

    fun harvestCompleteFlux(service: PrajService): Flux<Harvested>

    fun harvestTyped(harvestType: HarvestType, prajService: PrajService)=
            when(harvestType){
                HarvestType.COMPLETE->harvestCompleteFlux(prajService)
                HarvestType.DIFF->harvestFlux(prajService)
            }


}