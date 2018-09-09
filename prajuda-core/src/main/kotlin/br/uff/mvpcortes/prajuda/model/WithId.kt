package br.uff.mvpcortes.prajuda.model

/**
 * Class DTO to return values to client
 */
interface WithId{

    companion object {
        val COLUMN_ID = "id"
    }

    var id:String?

    private class DelegateWithId(withId: WithId): WithId by withId

    fun onlyId(): WithId = DelegateWithId(this)
}