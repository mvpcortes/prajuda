package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajDocument
interface PrajDocumentDAO {

    fun updateTag(serviceId: String, tag: String): Int
    fun delete(doc: PrajDocument): Int
    fun save(doc: PrajDocument):PrajDocument

}