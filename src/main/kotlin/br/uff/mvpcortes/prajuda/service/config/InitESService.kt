package br.uff.mvpcortes.prajuda.service.config

import br.uff.mvpcortes.prajuda.dao.HealthCheckDAO
import br.uff.mvpcortes.prajuda.dao.PrajConfigDAO
import br.uff.mvpcortes.prajuda.loggerFor
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class InitESService(val prajConfigDAO: PrajConfigDAO, val healthCheckDAO: HealthCheckDAO) {

    val logger = loggerFor(InitESService::class)

    fun initElasticSearch(){
        if(healthCheckDAO.healthCheck() == HealthCheckDAO.StatusCheck.UP) {
            //init config file
            prajConfigDAO.initConfigIfNotExists()
        }else{
            logger.warn("Cannot connect to ElasticSearch and cannot initialized  indexes and data")
        }
    }
}