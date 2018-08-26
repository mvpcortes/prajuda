package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.model.HarvesterType
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class PrajudaWorkerService {

    /**
     * It use a fake version. It should made a REST request to prworker to get installed harvesters
     */
    @Cacheable("PrajudaWOrkerService.mapHarvesterTypes")
    fun mapHarvesterTypes():Map<String, HarvesterType>{
        return mapOf(
                "git_classic" to HarvesterType(name="Git (Classic)", id="git_classic")
        )
    }

    fun harvesterTypes() = mapHarvesterTypes().values
}