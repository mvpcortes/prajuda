package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.dao

interface CrudDAO<T> {

    fun find(sKey:String):T?

    fun save(t:T):String

    fun delete(sKey:String):Boolean
}