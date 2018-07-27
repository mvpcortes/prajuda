package br.uff.mvpcortes.prajuda.dao.interfaces

import reactor.core.publisher.Mono

interface ISaveDAO<T> {

    fun save(t:T)

    fun saveAsync(t:T): Mono<Void>
}