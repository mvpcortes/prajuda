package br.uff.mvpcortes.prajuda.dao.highapi

import br.uff.mvpcortes.prajuda.dao.PrajConfigDAO
import br.uff.mvpcortes.prajuda.dao.highapi.template.HighApiTemplateImplDAO
import br.uff.mvpcortes.prajuda.model.PrajConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Repository

@Repository
class PrajConfigHighApiDAO(clientManager:ClientManager,
                           objectMapper: ObjectMapper

):PrajConfigDAO,
        HighApiTemplateImplDAO<PrajConfig>(PrajConfig::class,"prajuda.admin", "config", clientManager, objectMapper) {


    override fun get(): PrajConfig = super.get(PrajConfigDAO.DEFAULT_ID)

    override fun initConfigIfNotExists(){
        notExists(PrajConfigDAO.DEFAULT_ID){
            save(PrajConfig(id=PrajConfigDAO.DEFAULT_ID))
        }
    }

    override fun deleteConfig() {
        delete(PrajConfigDAO.DEFAULT_ID)
    }

    override fun detectInsertMethod(id:String?):Boolean{
        assert(id==PrajConfigDAO.DEFAULT_ID, {"cannot check a PrajConfig with different id: $id!=${PrajConfigDAO.DEFAULT_ID}"})
        return !exists(id!!)
    }

}