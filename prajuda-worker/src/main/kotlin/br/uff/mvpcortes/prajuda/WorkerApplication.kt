package br.uff.mvpcortes.prajuda

import br.uff.mvpcortes.prajuda.service.HarvesterEventService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@Import(br.uff.mvpcortes.prajuda.dao.impl.jdbc.PrajudaJdbcConfiguration::class)
@EnableScheduling
class PrajudaWorkerApplication

fun main(args: Array<String>) {

    runApplication<PrajudaWorkerApplication>(*args).use{
        it.getBean(HarvesterEventService::class.java).jobHarvesterEvent()
    }
}
