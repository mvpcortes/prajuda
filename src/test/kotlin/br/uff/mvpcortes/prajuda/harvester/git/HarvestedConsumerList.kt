package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedConsumer

class HarvestedConsumerList: ArrayList<Harvested>(10) {

    fun consumer():HarvestedConsumer = this::addU

    fun addU(h:Harvested){
        this.add(h)
    }
}