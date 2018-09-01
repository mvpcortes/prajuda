package br.uff.mvpcortes.prajuda

import br.uff.mvpcortes.prajuda.config.WorkerConfiguration
import br.uff.mvpcortes.prajuda.dao.HarvestRequestDAO
import br.uff.mvpcortes.prajuda.model.HarvestRequest
import br.uff.mvpcortes.prajuda.service.HarvestRequestService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(WorkerConfiguration::class, br.uff.mvpcortes.prajuda.dao.impl.jdbc.PrajudaJdbcConfiguration::class)
class PrajudaWorkerApplication

fun main(args: Array<String>) {

    runApplication<PrajudaWorkerApplication>(*args).use{
        it.getBean(HarvestRequestDAO::class.java).save(HarvestRequest(serviceSourceId = "2"))
        it.getBean(HarvestRequestDAO::class.java).save(HarvestRequest(serviceSourceId = "1"))
        it.getBean(HarvestRequestService::class.java).harvesterWorker()
    }
}
