package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi

import br.uff.mvpcortes.prajuda.loggerFor
import org.slf4j.Logger

class ClientConnectionTemplate(client: ClientManager, factory:RequestFactory){

    private val logger : Logger = loggerFor(this.javaClass)

    private val clientManager: ClientManager = client;

    private val requestFactory: RequestFactory = factory


    fun <T> run(func:()->T){
        val clientInstance = clientManager.getClient()
        try{
            func
        }catch(e:Exception){
            logger.error("Fail to execute operations on client conection", e);
        }finally {
            clientManager.releaseClient(clientInstance);
        }
    }
}