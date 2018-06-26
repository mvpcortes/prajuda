package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajDocument

typealias HarvestedConsumer = (Harvested)->Unit

class Harvested(val op: HarvestedOp,
                val doc: PrajDocument? = null
){
    /**
     * cast the document for a not-null value (with !! operator)
     */
    val sdoc:PrajDocument
        get() = doc!!
}