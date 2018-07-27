package br.uff.mvpcortes.prajuda.dao.highapi

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.stereotype.Service

/**
 * This class will manager the http clients to elastichsearch. In the future it may be a pool
 */
@Service
class ClientManager (val properties:ESHighApiProperties){


    /**
     * Return a http high client. The client should be closed after use
     */
    fun getClient(): RestHighLevelClient{
        val hosts  = getHosts()
        val builder = RestClient.builder(*hosts)
        return RestHighLevelClient(builder)
    }

    private fun getHosts(): Array<out HttpHost> = properties.listHosts().map{HttpHost(it.host, it.port!! , it.mode)}.toTypedArray()


    /**
     * Method with client auto-close. It is preferable to [getClient] without argument
     */
    fun <T> getClient(func:(RestHighLevelClient)->T):T=  getClient().use(func)
}