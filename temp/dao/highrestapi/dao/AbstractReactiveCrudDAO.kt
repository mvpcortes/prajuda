package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.dao

import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.ClientManager
import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.RequestFactory
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import reactor.core.publisher.Mono


class AbstractReactiveCrudDAO<T>(
        clazz:Class<T>,
        client: ClientManager,
        requestFactory: RequestFactory,
        @Qualifier("highRestApiObjectMapper") highRestApiObjectMapper: ObjectMapper)
    : ReactiveCrudDAO<T>{

    private class CrudDAO<T>(
            clazz:Class<T>,
            client: ClientManager,
            requestFactory: RequestFactory,highRestApiObjectMapper: ObjectMapper):
            AbstractCrudDAO<T>(clazz, client, requestFactory, highRestApiObjectMapper){

    }

    private val crudDAO = CrudDAO<T>(clazz, client, requestFactory, highRestApiObjectMapper)

    override fun find(sKey: String) = Mono.fromSupplier { crudDAO.find(sKey) }

    override fun save(t: T) = Mono.fromSupplier { crudDAO.save(t) }

    override fun delete(sKey: String)= Mono.fromSupplier {   crudDAO.delete(sKey)  }

}