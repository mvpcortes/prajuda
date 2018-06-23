package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient



class ClientManagerSimple(requestFactory:RequestFactory): ClientManager{

    private val clientTL: ThreadLocal<RestHighLevelClient> = ThreadLocal()

    private val requestFactory:RequestFactory= requestFactory

    override fun releaseClient(client: RestHighLevelClient) {
        checkNotNull(client){ "|clientManager| released cannot be null"}
        require(client.equals(clientTL.get())){"|clientManager| released should be equals to the threadlocal"}
        clientTL.remove();
    }

    override fun getClient(): RestHighLevelClient {
        return  if (clientTL.get()!= null) {createClient()}
                else{throw IllegalStateException("The clientManager there is create")}
    }

    fun createClient(): RestHighLevelClient{
        val client = RestHighLevelClient(RestClient.builder(
        HttpHost("localhost", 9200, "http"),
        HttpHost("localhost", 9201, "http")))
        clientTL.set(client);
        clientTL.get().search(null, null).
        return client;
    }

    override fun createTemplate(): ClientConnectionTemplate = ClientConnectionTemplate(this, requestFactory)

}