package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderImpl.Companion.STR_AJUDA_DIR
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName ="prajuda.admin", type="service")
data class PrajService(
        @Id var id:String?=null,
        val name:String = "",
        val url:String = "n/a",
        val harvesterTypeId:String="",
        val repositoryInfo: RepositoryInfo = RepositoryInfo(),
        val  documentDir:String = STR_AJUDA_DIR
){

    fun removeDocumentDir(str:String)=str.removePrefix(documentDir+"/")
}


