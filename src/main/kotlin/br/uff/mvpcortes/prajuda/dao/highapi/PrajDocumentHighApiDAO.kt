package br.uff.mvpcortes.prajuda.dao.highapi

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.dao.highapi.template.HighApiTemplateImplDAO
import br.uff.mvpcortes.prajuda.model.PrajDocument
import org.springframework.stereotype.Repository

@Repository
class PrajDocumentHighApiDAO (clientManager:ClientManager) :
        PrajDocumentDAO,
        HighApiTemplateImplDAO<PrajDocument>
(PrajDocument::class, "prajuda.md", "document", clientManager){

    override fun delete(doc: PrajDocument) {
    }
}