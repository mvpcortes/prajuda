package br.uff.mvpcortes.prajuda.dao.highapi

import java.io.Serializable
import kotlin.reflect.KClass

class EntityNotFoundException(val id:Serializable, val strClassName:String):RuntimeException("Cannot found ${strClassName} with id $id"){

    companion object {
        inline fun  <reified T> create(id:Serializable) = EntityNotFoundException(id, T::class.java.name)
    }
}