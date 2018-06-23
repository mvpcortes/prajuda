package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.dao

import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.ClientManager
import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.RequestFactory
import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.aop.ClientConnection
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.get.GetResponse
import org.springframework.data.annotation.Id
import java.lang.reflect.Field


abstract class AbstractCrudBase<T>(clazz:Class<T>,
                                   client:ClientManager,
                                   requestFactory: RequestFactory,
                                   highRestApiObjectMapper: ObjectMapper){
    protected val clazz:Class<T> = clazz
    protected val index:String = getIndex(clazz)
    protected val idField: Field = getIdField(clazz)
    protected val clientManager: ClientManager = client
    protected val requestFactory: RequestFactory = requestFactory
    protected val objectMapper: ObjectMapper = highRestApiObjectMapper



    private fun getIndex(clazz:Class<T>):String{
        return clazz.annotations.filter { a->a is ClientConnection }
                .map{ a-> a as ClientConnection }
                .map{cc->cc.index}
                .filter{ss->ss.isBlank()}
                .singleOrNull()?: throw IllegalStateException("Class ${clazz.name} should be '${ClientConnection::javaClass.name}' annotation with index value not blank')")
    }

    protected fun getId(t: T): String {
        return idField.get(t) as String
    }

    private fun getIdField(clazz:Class<T>): Field {
        return clazz.fields
                .filter{f->hasIdAnnotation(f)}
                .filter{f->f.type.isAssignableFrom(String.javaClass)}
                .singleOrNull()
                ?:throw IllegalStateException("Class ${clazz.name} should be '${Id::javaClass.name}' annotation on une Field String)")
    }


    private fun hasIdAnnotation(field: Field): Boolean {
        return !field.annotations.filter{a->a is Id}.isEmpty()
    }

    protected fun serialize(t:T):String{
        return objectMapper.writeValueAsString(t);
    }

    protected fun deserialize(response: GetResponse):T{
        return objectMapper.readValue(response.sourceAsString, clazz)
    }

}