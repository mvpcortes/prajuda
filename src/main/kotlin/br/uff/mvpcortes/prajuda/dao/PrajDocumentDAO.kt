package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface PrajDocumentDAO : ElasticsearchRepository<PrajDocument, String> {

    fun findByPath(oldPath: String):PrajDocument?
}