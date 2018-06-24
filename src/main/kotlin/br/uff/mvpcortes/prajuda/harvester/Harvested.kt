package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajDocument

typealias HarvestedConsumer = (Harvested)->Unit

class Harvested(val op: HarvesterProcessor.HarvestedOp,
                val doc: PrajDocument = PrajDocument()
)