package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.dao.interfaces.IGetDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import reactor.core.publisher.Mono
import java.util.*


interface PrajServiceDAO:IGetDAO<PrajService> {

    fun findIds(): List<String>

    @Deprecated("Try use a ? nullable kotlin or Mono/Reactive version")
    fun findById(ids: String): Optional<PrajService>

}