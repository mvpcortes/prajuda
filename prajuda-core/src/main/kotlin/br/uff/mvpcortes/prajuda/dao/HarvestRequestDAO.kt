package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.HarvestRequest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


interface HarvestRequestDAO{

    fun findById(id: String): HarvestRequest?

    /**
     * Get older open HarvestRequests
     */
    fun getOldOpen(qtd:Int): List<HarvestRequest>

    /**
     * Get and start old HarvestRequests
     */
    fun getAndStartOldOpen(qtd:Int): List<HarvestRequest>

    fun startRequests(startedDate: LocalDateTime, ids:List<String>): Int

    fun deleteAll():Int

    fun save(request: HarvestRequest): HarvestRequest

    fun completeRequests(ids: Collection<String>): Int

    fun failRequest(id: String, tw: Throwable): Int
}