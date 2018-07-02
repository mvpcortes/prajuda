package br.uff.mvpcortes.prajuda.dao.highapi

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.dao.highapi.template.HighApiTemplateImplDAO
import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PrajServiceHighApiDAO (clientManager:ClientManager) : PrajServiceDAO, HighApiTemplateImplDAO<PrajDocument>
(PrajDocument::class, "prajuda.admin", "service", clientManager){

    override fun findIds(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(ids: String): Optional<PrajService> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}