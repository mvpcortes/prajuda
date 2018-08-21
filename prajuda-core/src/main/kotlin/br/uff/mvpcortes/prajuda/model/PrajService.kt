package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.model.validation.URL
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderImpl.Companion.STR_AJUDA_DIR
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class PrajService(
        override var id:String?=null,
        @field:NotNull @field:NotBlank              val name:String = "",
        @field:NotNull @field:NotBlank @field:URL   val url:String = "n/a",
        @field:NotNull @field:NotBlank @Size(min=20, max=1000)  val description:String="Service Description",
        @field:NotNull @field:NotBlank              val harvesterTypeId:String="",
        @field:Valid val repositoryInfo: RepositoryInfo = RepositoryInfo(),
        @field:Pattern(regexp=REGEX_VALIDATION, message = "{br.uff.mvpcortes.prajuda.model.PrajService.documentDir.message}") @field:NotNull @field:NotBlank val documentDir:String = STR_AJUDA_DIR
):WithId{


    fun removeDocumentDir(str:String)=str.removePrefix("$documentDir/")

    companion object {
        const val REGEX_VALIDATION = "([\\w\\d\\.]+)(/[\\w\\d\\.]+)*(/)?"
        fun empty()=PrajService(null, "", "", "", "", RepositoryInfo())
    }
}


