package br.uff.mvpcortes.prajuda.api.dto

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Class DTO to return values to client
 */
interface WithId{

    val id:String?

    class DelegateWithId(withId:WithId):WithId by withId

    fun onlyId():WithId=DelegateWithId(this)
}