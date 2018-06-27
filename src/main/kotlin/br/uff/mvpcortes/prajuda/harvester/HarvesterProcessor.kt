package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajService

interface HarvesterProcessor {

    fun harvest(service: PrajService, blockDeal:HarvestedConsumer)

    fun harvestComplete(service: PrajService, blockDeal:HarvestedConsumer)

    /**
     * Verify if the path is one of the markdown types
     */
    fun acceptPath(path:String)= arrayOf(
                    Regex(".+.md"),
                    Regex(".+.markdown"),
                    Regex(".+.txt")
            ).filter { it.matches(path) }
                    .any()

}