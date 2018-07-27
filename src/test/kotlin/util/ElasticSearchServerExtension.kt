package util

import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.service.config.ElasticSearchServer
import org.junit.jupiter.api.extension.*
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic
import pl.allegro.tech.embeddedelasticsearch.PopularProperties
import java.io.File
import java.util.*

open class ElasticSearchServerExtension:
        BeforeAllCallback,
        AfterAllCallback,
        ParameterResolver {

    companion object {
        val ESSERVER_NAMESPACE = ExtensionContext.Namespace.create(ElasticSearchServerExtension::class)
    }


    var tcpPort = 9321

    var httpPort = 9320

    var clusterName = "test_cluster"

    val logger = loggerFor(ElasticSearchServer::class)


    data class TestESContext(val uniqueId:String, val embeddedElastic:EmbeddedElastic, val reuseCount:Int=1) {
        fun nextContext(uniqueId: String): ElasticSearchServerExtension.TestESContext =  TestESContext(uniqueId, this.embeddedElastic, this.reuseCount+1)

        fun isReusedContext() = reuseCount > 1
    }


    override fun beforeAll(context: ExtensionContext?) {

        context!!
        //Try create ESSERVER if not exists
        val stack = getStack(context)

        val testContext = if(stack.isEmpty()){
            logger.info("init ElasticSearch Server[httpPort={}, tcpPort={}]", httpPort, tcpPort)
            val builder = EmbeddedElastic.builder()
                    .withElasticVersion("6.2.4")
                    .withDownloadDirectory(getDownloadDirectory())
                    .withSetting(PopularProperties.TRANSPORT_TCP_PORT, tcpPort)
                    .withSetting(PopularProperties.HTTP_PORT, httpPort)
                    .withSetting(PopularProperties.CLUSTER_NAME, clusterName)

            applyIndexes(builder)

            val embeddedES = builder.build()
            embeddedES?.start()
            logger.info("Initialized Elastic Search Server")
            TestESContext(context.uniqueId, embeddedES)
        }else{
            logger.info("Reuse ESServer")
            stack.peek().nextContext(context.uniqueId)
        }
        stack.push(testContext)
    }

    private fun getStack(context: ExtensionContext): Stack<TestESContext> {
        return context.getStore(ESSERVER_NAMESPACE).getOrComputeIfAbsent(
                "ESSERVER_STACK",
                { Stack<TestESContext>() }) as Stack<TestESContext>
    }

    private fun getDownloadDirectory(): File? {
        val file = File(System.getProperty("user.dir"), "elasticsearch")
        if(!file.exists()){
            file.mkdirs()
        }
        return file
    }


    private fun applyIndexes(builder:EmbeddedElastic.Builder){
        builder.withIndex("prajuda.md")
        builder.withIndex("prajuda.admin")
    }

    override fun afterAll(context: ExtensionContext?) {

        context!!

        val testESContext = getStack(context).pop()

        if(testESContext.isReusedContext()){
            logger.info("Remove a context with ESServer")
        }else{
            logger.info("close ElasticSearch Server")
            testESContext.embeddedElastic.let {
                it.deleteIndices()
                it.stop()
            }
            logger.info("closed ElasticSearch Server")
        }

    }

    override fun supportsParameter(parameterContext: ParameterContext?, p1: ExtensionContext?): Boolean {
        return (parameterContext?.parameter?.type == EmbeddedElastic::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext?, p1: ExtensionContext?): Any? {
        return if(parameterContext?.parameter?.type == EmbeddedElastic::class.java){
            getStack(p1!!).peek().embeddedElastic
        }else{
            null
        }
    }

}