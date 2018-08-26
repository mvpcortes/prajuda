package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.config.WorkerProperties
import br.uff.mvpcortes.prajuda.dao.HarvestRequestDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.consumer.SaveHarvestedDB
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.HarvestRequest
import br.uff.mvpcortes.prajuda.workdir.WorkDirectoryService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class HarvestRequestService (
        val harvestRequestDAO: HarvestRequestDAO,
        val workDirectoryService: WorkDirectoryService,
        val workerProperties: WorkerProperties,
        val prajServiceDAO: PrajServiceDAO,
        val harvesterTypeService:HarvesterTypeService,
        val harvestedConsumer: SaveHarvestedDB){

    private val logger = loggerFor(HarvestRequestService::class)

    /**
     * Defines scheduler. We use single thread now. But in the future we will use the parallel or elastic
     */
    private val scheduler = workerProperties.buildScheduler()


    fun harvesterWorker(){
        logger.info("Using prajuda directory: {}", workDirectoryService.workDirectory())
        while(true) {
            internalHarvesterWorker()
            Thread.sleep(workerProperties.delayForHarvestRequest)
        }
    }

    class PairServiceTags(tag:String){
        private val tags:MutableMap<String, Long> = mutableMapOf(tag to 0L)
        val maxTag: String
                get()= tags.entries.sortedBy { it.value }.lastOrNull()?.key?:"N/A"


        fun inc(tag:String){
            synchronized(this) {
                tags.put(tag, tags.getOrDefault(tag, 0L)+1)
            }
        }
    }

    fun internalHarvesterWorker() {
        logger.info("Init worker loop")

        val havestRequestIds = ConcurrentHashMap.newKeySet<String>()
        val serviceTags = ConcurrentHashMap<String, PairServiceTags>()

        fluxHarvested()
                .log()
                .subscribeOn(scheduler)
                .doOnComplete {
                    harvestRequestDAO.completeRequests(LocalDateTime.now(), havestRequestIds)
                    serviceTags.forEach{
                        prajServiceDAO.updateTag(it.key, it.value.maxTag)
                    }
                }
                .subscribe { pair ->
                    havestRequestIds.add(pair.first.id)
                    updateTag(serviceTags, pair.second.doc.serviceId!!, pair.second.doc.tag)
                    harvestedConsumer.accept(pair.second)
                }


        logger.info("Finish worker loop")
    }

    private fun updateTag(serviceTags: ConcurrentHashMap<String, PairServiceTags>, servideId:String, tag: String) {
        val pair = serviceTags.getOrPut(servideId) { PairServiceTags(tag) }
        pair.inc(tag)
    }

    fun fluxHarvested(): Flux<Pair<HarvestRequest, Harvested>> {
        return this.fluxOpenHarvesters()
                .map { Pair(it, prajServiceDAO.findByIdNullable(it.serviceSourceId)!!) }
                .flatMap { requestAndService ->
                    harvesterTypeService.getHarvesterProcessor(requestAndService.second.harvesterTypeId)
                            .harvestTyped(requestAndService.first.harvestType, requestAndService.second)
                            .map { Pair(requestAndService.first, it) }
                }
    }

    /**
     * We use on request to let flux resolve any times call database
     * @see {http://projectreactor.io/docs/core/release/reference/#_hybrid_push_pull_model}
     */
    fun fluxOpenHarvesters(): Flux<HarvestRequest> {

        return Flux.generate{ sink->
            harvestRequestDAO.getAndStartOldOpen(workerProperties.maxHarvestRequest())
                    .forEach {
                            sink.next(it)
                        }
                        sink.complete()
                    }
        }
}