package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajCondig
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository

interface ConfigDAO: ElasticsearchCrudRepository<PrajCondig, String>{
}