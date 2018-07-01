package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.harvester.Harvested
import java.util.function.Consumer

class HarvestedConsumerList: ArrayList<Harvested>(10) {

    fun consumer():(Harvested)->Unit = this::addU

    private inner class ConsumerImpl:Consumer<Harvested>{
        override fun accept(p0: Harvested) =  addU(p0)
    }

    fun javaConsumer(): Consumer<Harvested> = ConsumerImpl()


    fun addU(h:Harvested){
        this.add(h)
    }

    fun sort()= this.sortWith (compareBy({it.op}, {it.doc?.path}) )

}