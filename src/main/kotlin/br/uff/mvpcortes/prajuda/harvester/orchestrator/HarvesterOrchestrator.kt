package br.uff.mvpcortes.prajuda.harvester.orchestrator

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.FluxHarvesterProcessor
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.loggerFor
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import reactor.core.scheduler.Schedulers
import java.util.function.Consumer

@Service
class HarvesterOrchestrator(val fluxHarvesterProcessor: FluxHarvesterProcessor,
                            val harvestedConsumer:Consumer<Harvested>,
                            val prajServiceDAO:PrajServiceDAO) {

    private val logger = loggerFor(HarvesterOrchestrator::class)


    companion object {
        /**
         * Number of harvesters should be do at same time
         */
        private val HARVESTER_WINDOW_COUNT = 10
    }

    /**
     * Define the orchestrator scheduler. We use single thread now. But in the future we will use the parallel or elastic
     */
    private val scheduler = Schedulers.immediate()


    fun harvesterComplete(){
        prajServiceDAO.findIds()
                .chunked(HARVESTER_WINDOW_COUNT)
                .forEachIndexed { i, subListService->
                    logger.info("INIT COMPLETE BLOCK {}/{}", i, HARVESTER_WINDOW_COUNT)
                    val fluxes = subListService.asSequence().map{idsToFluxComplete(it)}
                    harvester(fluxes)
                    logger.info("FINISH COMPLETE BLOCK {}/{}", i, HARVESTER_WINDOW_COUNT)
                }
    }

    fun harvesterDiff(){
        prajServiceDAO.findIds()
                .chunked(HARVESTER_WINDOW_COUNT)
                .forEachIndexed { i, subListService->
                    logger.info("INIT DIFF BLOCK {}/{}", i, HARVESTER_WINDOW_COUNT)
                    val fluxes = subListService.asSequence().map{idsToFluxDiff(it)}
                    harvester(fluxes)
                    logger.info("FINISH DIFF BLOCK {}/{}", i, HARVESTER_WINDOW_COUNT)
                }
    }

    private fun idsToFluxComplete(ids: String)= prajServiceDAO
            .findByIdNullable(ids)
            ?.let{fluxHarvesterProcessor.harvestCompleteFlux(it)}
            ?:Flux.empty()

    private fun idsToFluxDiff(ids: String)= prajServiceDAO
            .findByIdNullable(ids)
            ?.let{fluxHarvesterProcessor.harvestFlux(it)}
            ?:Flux.empty()

    /**
     * Harvester documents from a collections of flux.
     */
    fun harvester(fluxes:Sequence<Flux<Harvested>>){
        fluxes.toFlux()
                .flatMap { it }
                .log()
                .subscribeOn(this.scheduler)
                .subscribe(this.harvestedConsumer)
    }

}