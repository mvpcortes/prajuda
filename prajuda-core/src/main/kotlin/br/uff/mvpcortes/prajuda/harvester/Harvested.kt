package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajDocument

class Harvested(val op: HarvestedOp,
                val doc: PrajDocument
){
    override  fun toString()= "$op-${doc.path}"
}