package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.model.validation.URL
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

data class RepositoryInfo(
        @field:URL          val uri:String="",
        @field:NotBlank     val branch:String="master",
                            val lastModified: LocalDateTime = LocalDateTime.now(),
                            val lastTag:String?=null,
                            val username:String="",
        @field:NotBlank     val password:String="") {

    fun hasCredentials(): Boolean = password.isBlank()
}