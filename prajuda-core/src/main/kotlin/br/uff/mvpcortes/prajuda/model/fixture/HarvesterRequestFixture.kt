package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.HarvestRequest


class HarvesterRequestFixture{
    companion object {
        fun open(serviceId:String="my-service") = HarvestRequest(serviceSourceId = serviceId)
        fun started() = open().toStarted()
        fun completed() = started().toCompleted()
    }
}