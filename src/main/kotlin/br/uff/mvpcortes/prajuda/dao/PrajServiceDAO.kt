package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajService


interface PrajServiceDAO {
    fun findIds(): List<String>
    fun findByIdNullable(ids: String): PrajService?
    fun save(prajService: PrajService): PrajService
}