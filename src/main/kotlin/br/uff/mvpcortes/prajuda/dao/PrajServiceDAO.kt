package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajService
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface PrajServiceDAO: ElasticsearchRepository<PrajService, String> {

    fun findIds(): List<String>
}