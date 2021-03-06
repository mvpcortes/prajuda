package br.uff.mvpcortes.prajuda.harvester.consumer

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.loggerFor
import org.springframework.stereotype.Service
import java.util.function.Consumer

interface SaveHarvestedDB:Consumer<Harvested>

@Service
class HarvestedConsumerToDB (val prajDocumentDAO: PrajDocumentDAO) : SaveHarvestedDB{

    val logger = loggerFor(PrajDocumentDAO::class)


    override fun accept(h: Harvested) {
        when (h.op) {
            HarvestedOp.DELETED -> {prajDocumentDAO.deleteTrackingServiceAndPath(h.doc)}
            HarvestedOp.UPDATED -> {prajDocumentDAO.saveTrackingServiceAndPath(h.doc)}
            HarvestedOp.NO_OP -> { logger.warn("Found a noop: {}", h.doc)}
        }
    }

}