package br.uff.mvpcortes.prajuda.dao.highapi.template

import br.uff.mvpcortes.prajuda.dao.highapi.ClientManager
import br.uff.mvpcortes.prajuda.dao.highapi.EntityNotFoundException
import br.uff.mvpcortes.prajuda.dao.highapi.request.RequestFactory
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajConfig
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import org.elasticsearch.ElasticsearchStatusException
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.common.xcontent.XContentType
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.jvmErasure


abstract class HighApiTemplateImplDAO<T : Any>(protected val kclass:KClass<T> ,
                                               protected val clientManager: ClientManager,
                                               protected val objectMapper: ObjectMapper,
                                               protected val requestFactory: RequestFactory,
                                               protected val propId: KMutableProperty1<T, String?> = getIdProperty(kclass))
    :GetTemplateDAO<T>, ExistsTemplateDAO, SaveTemplateDAO<T>, DeleteTemplateDAO{

    val logger = loggerFor(HighApiTemplateImplDAO::class)

    constructor(kclass:KClass<T>,
                index:String,
                type:String,
                clientManager: ClientManager,
                objectMapper:ObjectMapper=ObjectMapper()):this(kclass, clientManager, objectMapper, RequestFactory(index, type))


    private fun defaultObjectMapper():ObjectMapper{
        val theFilter = SimpleBeanPropertyFilter
                .serializeAllExcept("id")
        val filters = SimpleFilterProvider()
                .addFilter("myFilter", theFilter)
        return ObjectMapper().setFilterProvider(filters)

    }


    override fun get(id:String): T{
        return clientManager.getClient { client->
            client.get(requestFactory.createGet(id))
                    .takeIf{ it.isExists }
                    ?.let{Pair(objectMapper.readValue(it.sourceAsBytes,kclass.java), it)}
                    ?.let{setId(it)}
                    ?:throw EntityNotFoundException.create<PrajConfig>(id)
        }
    }

    private fun setId(pp: Pair<T, GetResponse>): T {
        propId.set(pp.first, pp.second.id)
        return pp.first
    }

    override fun exists(id: String): Boolean{
        return clientManager.getClient { client ->
            client.exists(requestFactory.createExists(id))
        }
    }

    override fun save(t:T){
        val id = getId(t)

        clientManager.getClient {client ->
            val updateRequest = requestFactory
                    .createUpdate(id)
                    .doc(getJsonArrayByte(t), XContentType.JSON)
                    .docAsUpsert(detectInsertMethod(id))

            client.update(updateRequest)
        }
    }

    protected open fun detectInsertMethod(id: String?) = id != null


    override fun delete(id:String){
        clientManager.getClient {
            try {
                it.delete(requestFactory.createDelete(id))
            }catch(e: ElasticsearchStatusException){
                logger.warn("delete fail. Index exists?")
            }
        }
    }

    fun getJsonArrayByte(t:T){
        objectMapper.writeValueAsBytes(t)
    }

    private fun getId(t: T) = propId.get(t)

}

private fun <T : Any> getIdProperty(kclass: KClass<T>): KMutableProperty1<T, String?> =
        kclass.memberProperties.asSequence()
                .filter{ it.returnType.jvmErasure == String::class}
                .filter{ it.name == "id"}
                .filter{ it is  KMutableProperty1 }
                .map   { it as KMutableProperty1<T, String?> }
                .singleOrNull()
                ?:throw IllegalStateException("Cannot found mutable property 'id'")


