package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.HarvestRequest
import br.uff.mvpcortes.prajuda.model.HarvestType


class HarvesterRequestFixture{
    companion object {
        fun open(serviceId:String="my-service", harvestType: HarvestType=HarvestType.COMPLETE) = HarvestRequest(harvestType = harvestType, serviceSourceId = serviceId)
        fun started() = open().toStarted()
        fun completed() = started().toCompleted()
    }
}