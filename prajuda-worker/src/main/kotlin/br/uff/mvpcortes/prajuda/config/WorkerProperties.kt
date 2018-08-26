package br.uff.mvpcortes.prajuda.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

@Component
@ConfigurationProperties("prajuda.worker")
class WorkerProperties (
        val threadCount:Int=4,
        val delayForHarvestRequest: Long = 15000,
        val workerDir:String? = null,
        val scheduler:WorkerScheduler=WorkerScheduler.IMMEDIATE){


    enum class WorkerScheduler(val schedulerFactory:()-> Scheduler){
        IMMEDIATE(Schedulers::immediate),
        ELASTIC(Schedulers::elastic);

        fun build():Scheduler = schedulerFactory.invoke()
    }

    /**
     * Return max harvest to get form database on harvester cycle
     */
    fun maxHarvestRequest(): Int = (threadCount*2)

    fun buildScheduler() = scheduler.build()

}