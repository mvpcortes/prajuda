package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.config.WorkerProperties
import br.uff.mvpcortes.prajuda.dao.HarvestRequestDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.harvester.subscriber.FinishHarvestRequestSubscriber
import br.uff.mvpcortes.prajuda.harvester.subscriber.UpdateDatabaseSubscriber
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.HarvestRequest
import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.workdir.WorkDirectoryService
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class HarvestRequestService (
        val harvestRequestDAO: HarvestRequestDAO,
        val workDirectoryService: WorkDirectoryService,
        val workerProperties: WorkerProperties,
        val prajServiceDAO: PrajServiceDAO,
        val harvesterTypeService:HarvesterTypeService,
        val finishHarvestRequestSubscriberFactory: ObjectFactory<FinishHarvestRequestSubscriber>,
        val updateDatabaseSubscriber: UpdateDatabaseSubscriber){

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


    /**
     * get harvestRequest, do harvester and publish result to two subscribers:
     * updateDatabaseSubscriber will save data to DB
     * finishHarvestRequestSubscriber will collect processed requests and services to update tag/status in the end
     */
    fun internalHarvesterWorker() {
        logger.info("Init worker loop")

        val connectableFlux = fluxHarvested()
                .log()
                .subscribeOn(scheduler)
                .publish()

        connectableFlux.subscribe(this.updateDatabaseSubscriber)
        connectableFlux.subscribe(this.finishHarvestRequestSubscriberFactory.`object`)
        connectableFlux.connect()

        logger.info("Finish worker loop")
    }

    fun fluxHarvested(): Flux<Pair<HarvestRequest, Harvested>> {
        return this.fluxOpenHarvesters()
                .map { Pair(it, prajServiceDAO.findByIdNullable(it.serviceSourceId)!!) }
                .flatMap { requestAndService ->
                    try {
                        harvesterTypeService.getHarvesterProcessor(requestAndService.second.harvesterTypeId)
                                .harvestTyped(requestAndService.first.harvestType, requestAndService.second)
                                .map { Pair(requestAndService.first, it) }
                    }catch(e:Exception){
                        /** map fail to be processed by {UpdateDatabaseSubscriber}*/
                        Flux.just(Pair(requestAndService.first,
                                Harvested(op=HarvestedOp.FAIL, doc= PrajDocument(), exception = e)
                        ))
                    }
                }
    }

    /**
     * We use on request to let flux resolve any times call database
     * @see {http://projectreactor.io/docs/core/release/reference/#_hybrid_push_pull_model}
     */
    fun fluxOpenHarvesters(): Flux<HarvestRequest> =
        Flux.create {sink->
            harvestRequestDAO.getAndStartOldOpen(workerProperties.maxHarvestRequest())
                    .forEach {
                        sink.next(it)
                    }
            sink.complete()
        }
}