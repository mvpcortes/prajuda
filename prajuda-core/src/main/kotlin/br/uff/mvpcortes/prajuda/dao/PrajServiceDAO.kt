package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajService
import reactor.core.publisher.Flux



interface PrajServiceDAO {

    fun findIds(): List<String>

    fun findByIdNullable(id: String): PrajService?

    fun save(prajService: PrajService): PrajService

    fun findPage(page: Int, pageSize: Int): Flux<PrajService>

    fun count(): Long

    fun findByIds(vararg ids: String): Flux<PrajService>

    fun delete(id: String): Int

    fun updateTag(id: String, tag: String):Int
}