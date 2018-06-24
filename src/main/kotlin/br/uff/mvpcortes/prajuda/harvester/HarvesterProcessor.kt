package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.modelService.PrajService

interface HarvesterProcessor {

    enum class HarvestedOp{
        UPDATED(),
        DELETED(),
        NO_OP()
    }


    fun harvest(service: PrajService, blockDeal:HarvestedConsumer)

    fun harvestComplete(service: PrajService, blockDeal:HarvestedConsumer)

}