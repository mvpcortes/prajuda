package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.model.validation.url.URL
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderImpl.Companion.STR_AJUDA_DIR

data class PrajService(
        var id:String?=null,
        val name:String = "",
        @URL val url:String = "n/a",
        val harvesterTypeId:String="",
        val repositoryInfo: RepositoryInfo = RepositoryInfo(),
        val documentDir:String = STR_AJUDA_DIR
){

    fun removeDocumentDir(str:String)=str.removePrefix("$documentDir/")

    companion object {
        fun empty()=PrajService(null, "", "", "", RepositoryInfo())
    }
}


