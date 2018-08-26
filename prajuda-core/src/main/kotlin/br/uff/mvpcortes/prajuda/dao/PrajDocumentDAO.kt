package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajDocument
interface PrajDocumentDAO {

    fun updateTag(serviceId: String, tag: String): Int

    fun delete(doc: PrajDocument): Int

    fun save(doc: PrajDocument):PrajDocument

    /**
     * Find all documents from a service (it return a list because is used to tests)
     */
    fun findByService(id: String):List<PrajDocument>

    fun deleteByServiceId(id: String)

}