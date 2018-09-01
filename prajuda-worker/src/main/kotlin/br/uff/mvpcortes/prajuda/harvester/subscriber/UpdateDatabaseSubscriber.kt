package br.uff.mvpcortes.prajuda.harvester.subscriber

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.HarvestRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.BaseSubscriber

/**
 * Subscriber that save havested operations {HarvestedOp}
 */
@Service
class UpdateDatabaseSubscriber(val prajDocumentDAO: PrajDocumentDAO):
        BaseSubscriber<Pair<HarvestRequest, Harvested>>() {

    val logger = loggerFor(PrajDocumentDAO::class)

    override fun hookOnNext(pairH: Pair<HarvestRequest, Harvested>) {
        val op  = pairH.second.op
        val doc = pairH.second.doc
        when (op) {
            HarvestedOp.DELETED -> {prajDocumentDAO.deleteTrackingServiceAndPath(doc)}
            HarvestedOp.UPDATED -> {prajDocumentDAO.saveTrackingServiceAndPath(doc)}
            HarvestedOp.NO_OP -> { logger.warn("Found a noop: {}", doc)}
        }
    }
}