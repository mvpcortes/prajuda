package br.uff.mvpcortes.prajuda.harvester.subscriber

import br.uff.mvpcortes.prajuda.dao.HarvestRequestDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.HarvestRequest
import org.slf4j.Logger
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.BaseSubscriber
import java.util.concurrent.ConcurrentHashMap

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class FinishHarvestRequestSubscriber(
        val harvestRequestDAO:HarvestRequestDAO,
        val prajServiceDAO:PrajServiceDAO,
        val transactionTemplate: TransactionTemplate,
        private val logger: Logger = loggerFor(FinishHarvestRequestSubscriber::class)
): BaseSubscriber<Pair<HarvestRequest, Harvested>>() {

    val harvestRequestIds = ConcurrentHashMap.newKeySet<String>()!!
    val harvestRequestFailIds
            = ConcurrentHashMap<String, Exception>()
    val serviceTag = ConcurrentHashMap<String, String>()

    override fun hookOnCancel() {
        logger.error("flux had cancelled. Register fail on request")
        val sequenceRequestIds = harvestRequestIds
                .asSequence()
                .map{ Pair( it,IllegalStateException("flux had cancelled")            ) }

        val concatenedSequence = sequenceRequestIds + harvestRequestFailIds.entries.asSequence().map{Pair(it.key, it.value)}

        concatenedSequence.forEach { harvestRequestDAO.failRequest(it.first, it.second) }
    }

    override fun hookOnComplete() {
        logger.info("complete change request state")
        transactionTemplate.execute {
            harvestRequestFailIds.forEach {
                harvestRequestDAO.failRequest(it.key, it.value)
                harvestRequestIds.remove(it.key)//remove failed ids
            }

            harvestRequestDAO.completeRequests(harvestRequestIds)

            serviceTag.forEach {
                prajServiceDAO.updateTag(it.key, it.value)
            }
        }

        logger.info("Finish harvester. Has {} successed requests , [{}] failed requests",
                harvestRequestIds,
                harvestRequestFailIds.asSequence().map{it.key}.toList()
        )
    }


    override fun hookOnNext(pair: Pair<HarvestRequest, Harvested>) {
        if(pair.second.op == HarvestedOp.FAIL){
            harvestRequestFailIds[pair.first.id!!]= pair.second.exception!!
        }else {
            harvestRequestIds.add(pair.first.id)
            //Many times last get tag is the greater tag. Then we will consider this heuristic
            serviceTag[pair.first.serviceSourceId] = pair.second.doc.tag
        }
    }
}