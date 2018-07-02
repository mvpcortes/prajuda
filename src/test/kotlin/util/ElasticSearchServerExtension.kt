package util

import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.service.config.DefIndexES
import br.uff.mvpcortes.prajuda.service.config.ElasticSearchServer
import org.junit.jupiter.api.extension.*
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic
import pl.allegro.tech.embeddedelasticsearch.PopularProperties
import java.io.File

open class ElasticSearchServerExtension:
BeforeAllCallback,
AfterAllCallback,
ParameterResolver {

    var embeddedES : EmbeddedElastic?=null

    var tcpPort = 9321

    var httpPort = 9320

    var clusterName = "test_cluster"

    val logger = loggerFor(ElasticSearchServer::class)

    val indexes:List<DefIndexES> = listOf()

    override fun beforeAll(context: ExtensionContext?) {
        logger.info("init ElasticSearch Server[httpPort={}, tcpPort={}]", httpPort, tcpPort)
        val builder = EmbeddedElastic.builder()
                .withElasticVersion("6.2.4")
                .withDownloadDirectory(getDownloadDirectory())
                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, tcpPort)
                .withSetting(PopularProperties.HTTP_PORT, httpPort)
                .withSetting(PopularProperties.CLUSTER_NAME, clusterName)

        applyIndexes(builder)

        embeddedES = builder.build()
        embeddedES?.start()
        logger.info("Initied Elastic Search Server")
    }

    private fun getDownloadDirectory(): File? {
        val file = File(System.getProperty("user.dir"), "elasticsearch")
        if(!file.exists()){
            file.mkdirs()
        }
        return file
    }


    private fun applyIndexes(builder:EmbeddedElastic.Builder){
        indexes.forEach{
            builder.withIndex(it.name)
        }
    }

    override fun afterAll(context: ExtensionContext?) {
        logger.info("close ElasticSearch Server")
        embeddedES?.let {
            it.deleteIndices()
            it.stop()
        }

        logger.info("closed ElasticSearch Server")
    }

    override fun supportsParameter(parameterContext: ParameterContext?, p1: ExtensionContext?): Boolean {
        return (parameterContext?.parameter?.type == EmbeddedElastic::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext?, p1: ExtensionContext?): Any? {
        return if(parameterContext?.parameter?.type == EmbeddedElastic::class.java){
                    embeddedES!!
                }else{
                    null
                }
    }

}