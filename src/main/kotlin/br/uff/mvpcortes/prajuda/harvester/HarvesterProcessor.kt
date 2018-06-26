package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajService

interface HarvesterProcessor {

    fun harvest(service: PrajService, blockDeal:HarvestedConsumer)

    fun harvestComplete(service: PrajService, blockDeal:HarvestedConsumer)

}