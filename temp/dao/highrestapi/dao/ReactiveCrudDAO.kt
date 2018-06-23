package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.dao

import reactor.core.publisher.Mono

interface ReactiveCrudDAO<T> {

    fun find(sKey:String): Mono<T?>

    fun save(t:T):Mono<String>

    fun delete(sKey:String):Mono<Boolean>

}