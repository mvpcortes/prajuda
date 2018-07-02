package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajDocument

interface PrajDocumentDAO {

    fun updateTag(serviceId: String, tag: String) {
        TODO("Remove this method to the impl class")
    }

    fun delete(doc: PrajDocument)

    fun save(doc: PrajDocument)
}