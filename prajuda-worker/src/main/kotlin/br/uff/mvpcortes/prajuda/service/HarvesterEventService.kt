package br.uff.mvpcortes.prajuda.service

import org.springframework.stereotype.Service

@Service
class HarvesterEventService{
    fun jobHarvesterEvent() {
        Thread.sleep(10000L);
    }

}