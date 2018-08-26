package br.uff.mvpcortes.prajuda.service

import org.springframework.stereotype.Component

@Component
class ScheduleService(val harvesterSingleRunSection: HarvesterSingleRunSection){

    /**
     * Sessão Excludente
     * sucesso
     * ocupado
     * falha
     */

//    @Scheduled(fixedDelay= 86400000L)
    fun runHavester(){
        harvesterSingleRunSection.harvesterDiff()
    }
}