package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi

import org.elasticsearch.client.RestHighLevelClient

interface ClientManager{

    fun getClient(): RestHighLevelClient

    fun releaseClient(client:RestHighLevelClient)

    fun createTemplate():ClientConnectionTemplate

    fun <T> runTemplate(func:()->T){
        createTemplate().run(func)
    }
}