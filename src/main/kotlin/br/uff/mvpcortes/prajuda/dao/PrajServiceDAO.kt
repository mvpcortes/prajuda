package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajService


interface PrajServiceDAO {

    fun findIds(): List<String>

    fun findByIdNullable(id: String): PrajService?

    fun save(prajService: PrajService): PrajService
}