package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.model.validation.URL
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
        @field:Pattern(
                regexp=REGEX_VALIDATION,
                message = "{br.uff.mvpcortes.prajuda.model.PrajService.documentDir.message}") @field:NotNull @field:NotBlank
                val documentDir:String = STR_AJUDA_DIR,
        @field:Pattern(
                regexp=REGEX_PATH_NAME_VALIDATION,
                message = "{br.uff.mvpcortes.prajuda.model.PrajService.namePath.message}") @field:NotNull
                val namePath:String=sanitizeName(name)
):WithId{


    fun removeDocumentDir(str:String)=str.removePrefix("$documentDir/")

    companion object {
        const val STR_AJUDA_DIR = "prajuda"
        const val REGEX_VALIDATION = "([\\w\\d]+)(/[\\w\\d]+)*(/)?"
        const val REGEX_PATH_NAME_VALIDATION = "([\\w_]+)"
        fun empty()=PrajService(null, "", "", "", "", RepositoryInfo())

        fun sanitizeName(name: String): String =
            name.replace(Regex("[^\\w_]"), "_").trim()
    }
}




