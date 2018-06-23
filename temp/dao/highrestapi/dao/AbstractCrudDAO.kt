package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.dao

import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.ClientManager
import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.RequestFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.DocWriteResponse
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.common.xcontent.XContentType
import org.springframework.beans.factory.annotation.Qualifier

abstract class AbstractCrudDAO<T>(
        clazz:Class<T>,
        client: ClientManager,
        requestFactory:RequestFactory,
        @Qualifier("highRestApiObjectMapper") highRestApiObjectMapper: ObjectMapper)
    :AbstractCrudBase<T>(clazz, client, requestFactory, highRestApiObjectMapper), CrudDAO<T>{


    override fun find(sKey: String): T? {
        val response = clientManager.getClient().get(
                requestFactory.getRequest(index).id(sKey)
        );
        return if (response.isExists()) deserialize(response) else null
    }


    override fun save(t: T):String = clientManager.getClient().update(insertRequest(t)).id

    override fun delete(sKey: String) = clientManager.getClient().delete(requestFactory.deleteRequest(index).id(sKey)).result != DocWriteResponse.Result.NOT_FOUND

    /**
     * Create request for insert/update
     */
    private fun insertRequest(t:T): UpdateRequest {
        var id:String = getId(t)
        return  if(id!=null) requestFactory.updateRequest(index).id(id).doc(serialize(t), XContentType.JSON)
                else  requestFactory.updateRequest(index).upsert(serialize(t), XContentType.JSON)
    }
}