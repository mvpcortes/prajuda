package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajConfig
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository

interface ConfigDAO: ElasticsearchCrudRepository<PrajConfig, String>