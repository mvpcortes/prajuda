package br.uff.mvpcortes.prajuda.model

/**
 * Class DTO to return values to client
 */
interface WithId{

    val id:String?

    class DelegateWithId(withId: WithId): WithId by withId

    fun onlyId(): WithId = DelegateWithId(this)
}