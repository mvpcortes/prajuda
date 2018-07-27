package br.uff.mvpcortes.prajuda.dao.interfaces

import reactor.core.publisher.Mono

interface IGetDAO <T> {

    fun get(id:String):T

    fun findMonoById(id:String): Mono<T>

}