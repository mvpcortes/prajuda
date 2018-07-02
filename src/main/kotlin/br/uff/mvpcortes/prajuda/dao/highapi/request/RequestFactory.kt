package br.uff.mvpcortes.prajuda.dao.highapi.request

import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.search.fetch.subphase.FetchSourceContext
import org.elasticsearch.action.update.UpdateRequest



class RequestFactory(val index:String, val type:String){

    fun createIndex( id:String)= IndexRequest(index, type, id)

    fun createGet( id:String)= GetRequest(index, type, id)

    fun createExists(id: String): GetRequest? {
        val getRequest = createGet(id)
        getRequest.fetchSourceContext(FetchSourceContext(false));
        getRequest.storedFields("_none_");
        return getRequest
    }

    fun createUpdate(id: String?) = UpdateRequest( index, type, id)

    fun createDelete(id: String) = DeleteRequest(index, type, id)
}