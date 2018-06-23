package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi

import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.search.fetch.subphase.FetchSourceContext

/**
 * Default RequestFactory implementation
 */
class RequestFactoryImpl: RequestFactory{

    override fun indexRequest(index: String): IndexRequest = IndexRequest(index)


    override fun getRequest(index: String): GetRequest = GetRequest(index)

    /**
     * @see <a href=https://www.elastic.co/guide/en/elasticsearch/clientManager/java-rest/current/java-rest-high-document-exists.html#java-rest-high-document-exists-request>exists method</a>
     */
    override fun existsRequest(index: String): GetRequest = GetRequest(index).fetchSourceContext(FetchSourceContext(false)).storedFields("_none_")

    override fun updateRequest(index: String): UpdateRequest = UpdateRequest().index(index)

    override fun searchRequest(index: String): SearchRequest = SearchRequest(index)

    override fun deleteRequest(index:String): DeleteRequest = DeleteRequest(index)
}