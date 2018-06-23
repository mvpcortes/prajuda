package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajDocument
import reactor.core.publisher.Flux

interface HarvesterProcessor {

    enum class HarvestedOp{
        UPDATED(),
        DELETED(),
        NO_OP()
    }

//    enum class HarvesterMode{
//        DIFF,/*Only havest the diff documents*/
//        COMPLETE,/*havest all documents*/
//        COMPLETE_REBUILD/*havest all documents rebuild the cached/local repository data*/
//    }

    class Harvested(val op:HarvestedOp, val doc:PrajDocument=PrajDocument()){}

    fun harvest(): Flux<PrajDocument>

    fun harvest(blockDeal:(Harvested)->Unit)

    fun harvestComplete(blockDeal:(Harvested)->Unit)

    fun getIdHarvester():String

}