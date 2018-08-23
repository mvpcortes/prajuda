package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.harvester.orchestrator.HarvesterOrchestrator
import br.uff.mvpcortes.prajuda.loggerFor
import org.springframework.stereotype.Service

@Service
class HarvesterSingleRunSection(
        val harvesterOrchestrator: HarvesterOrchestrator) {

    val singleRunSection = SingleRunSection()

    val logger = loggerFor(HarvesterSingleRunSection::class)
    fun harvesterComplete(){
        singleRunSection.tryExecute(
                {harvesterOrchestrator.harvesterComplete()},
                {logger.info("complete harvester complete finished")},
                {logger.warn("Already running")});

    }

    fun harvesterDiff(){
        singleRunSection.tryExecute(
                {harvesterOrchestrator.harvesterComplete()},
                {logger.info("complete harvester diff finished")},
                {logger.warn("Already running")});
    }
}