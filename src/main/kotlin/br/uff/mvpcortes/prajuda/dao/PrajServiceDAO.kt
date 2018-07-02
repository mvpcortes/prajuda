package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajService
import java.util.*


interface PrajServiceDAO {

    fun findIds(): List<String>

    @Deprecated("Try use a ? nullable kotlin")
    fun findById(ids: String): Optional<PrajService>

}