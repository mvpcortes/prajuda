package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi

import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.update.UpdateRequest

interface RequestFactory {

    fun indexRequest(index: String): IndexRequest

    fun getRequest(index: String): GetRequest

    fun existsRequest(index: String): GetRequest

    fun updateRequest(index: String): UpdateRequest

    fun searchRequest(index: String): SearchRequest

    fun deleteRequest(index:String): DeleteRequest

    //TODO: investigate if we need it
    //MultiGetRequest multiGetRequest(String index);
}