package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PrajDocumentDAO {

    fun updateTag(serviceId: String, tag:String): Int

    fun deleteTrackingServiceAndPath(doc:PrajDocument):Int

    fun delete(doc: PrajDocument): Int

    fun save(doc: PrajDocument):PrajDocument

    /**
     * We need save a document based on the service id and namePath. This method before save search by document id
     * to update or createHelper it
     */
    fun saveTrackingServiceAndPath(doc:PrajDocument):PrajDocument

    /**
     * Find all documents from a service (it return a list because is used to tests)
     */
    fun findByService(id: String):List<PrajDocument>

    fun deleteByServiceId(id: String)

    /**
     * Delete all documents. Used to test
     */
    fun deleteAll()

    /**
     * return qtd of documents in DB. Used to tests
     */
    fun count(): Long

    fun findById(id: String): PrajDocument?


    fun existsByServiceNamePathAndPath(serviceName: String, path: String): Boolean

    fun findByServiceNamePathAndPath(serviceName: String, path: String): PrajDocument?

    fun findDocById(documentId: String): Flux<String>

}