package br.uff.mvpcortes.prajuda.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("prajuda.worker")
class WorkerProperties (val threadCount:Int=4, val delayForHarvestRequest: Long = 15*60*1000){
    fun maxHarvestRequest(): Int = (threadCount*2)


}