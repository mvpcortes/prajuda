package br.uff.mvpcortes.prajuda.dao.highapi.template

import br.uff.mvpcortes.prajuda.dao.highapi.ClientManager
import br.uff.mvpcortes.prajuda.dao.highapi.EntityNotFoundException
import br.uff.mvpcortes.prajuda.dao.highapi.request.RequestFactory
import br.uff.mvpcortes.prajuda.dao.interfaces.IDeleteDAO
import br.uff.mvpcortes.prajuda.dao.interfaces.IExistsDAO
import br.uff.mvpcortes.prajuda.dao.interfaces.IGetDAO
import br.uff.mvpcortes.prajuda.dao.interfaces.ISaveDAO
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajConfig
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import org.elasticsearch.ElasticsearchStatusException
import org.elasticsearch.action.Action
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.action.update.UpdateResponse
import org.elasticsearch.client.Response
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.CheckedConsumer
import org.elasticsearch.common.xcontent.XContentType
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import java.util.function.Consumer
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.jvmErasure


abstract class HighApiImplDAO<T : Any>(protected val kclass:KClass<T>,
                                       protected val clientManager: ClientManager,
                                       protected val objectMapper: ObjectMapper,
                                       protected val requestFactory: RequestFactory,
                                       protected val propId: KMutableProperty1<T, String?> = getIdProperty(kclass))
    : IGetDAO<T>, IExistsDAO, ISaveDAO<T>, IDeleteDAO {

    val logger = loggerFor(HighApiImplDAO::class)

    constructor(kclass:KClass<T>,
                index:String,
                type:String,
                clientManager: ClientManager,
                objectMapper:ObjectMapper=ObjectMapper()):
            this(kclass, clientManager, objectMapper, RequestFactory(index, type))


    private fun defaultObjectMapper():ObjectMapper{
        val theFilter = SimpleBeanPropertyFilter
                .serializeAllExcept("id")
        val filters = SimpleFilterProvider()
                .addFilter("myFilter", theFilter)
        return ObjectMapper().setFilterProvider(filters)

    }


    override fun get(id:String): T{
        return clientManager.getClient { client->
            deserializeResponse(client.get(requestFactory.createGet(id)))
                    ?:throw EntityNotFoundException.create<PrajConfig>(id)
        }
    }

    private fun deserializeResponse(getResponse:GetResponse)= getResponse.takeIf{ it.isExists }
            ?.let{Pair(objectMapper.readValue(it.sourceAsBytes,kclass.java), it)}
            ?.let{setId(it)}


    /**
     * @see javadoc of [Mono.create]
     */
    override fun findMonoById(id:String):Mono<T> {
        return Mono.create<T>{sink->
            val actionListener = ActionListener.wrap(CheckedConsumer<GetResponse, Exception>{
                sink.success(deserializeResponse(it))
            }, Consumer<Exception>(sink::error))

            val client = clientManager.getClient()
            client.getAsync(requestFactory.createGet(id), actionListener)


            sink.onDispose { client.close() }
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
        clientManager.getClient {client ->
            val updateRequest = createUpdateRequest(getId(t), t)

            client.update(updateRequest)
        }
    }

    private fun createUpdateRequest(id: String?, t: T): UpdateRequest? {
        return requestFactory
                .createUpdate(id)
                .doc(getJsonArrayByte(t), XContentType.JSON)
                .docAsUpsert(detectInsertMethod(id))
    }

    override fun saveAsync(t: T): Mono<Void> {
        return Mono.create<Void>{sink->
            val actionListener =
                    ActionListener.wrap(CheckedConsumer<UpdateResponse, Exception>{ sink.success() },
                            Consumer<Exception>(sink::error))

            val client = clientManager.getClient()
            client.updateAsync(createUpdateRequest(getId(t), t), actionListener)
            sink.onDispose { client.close() }
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


