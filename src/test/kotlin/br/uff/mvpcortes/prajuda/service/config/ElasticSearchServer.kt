package br.uff.mvpcortes.prajuda.service.config

import br.uff.mvpcortes.prajuda.loggerFor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic
import pl.allegro.tech.embeddedelasticsearch.PopularProperties
import java.io.File
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

//@Component()
class ElasticSearchServer(@Value("\${elasticsearch.download_dir:}") val downloadDir:String, val indexes:List<DefIndexES>){

//
//    @Configuration
//    class DefaultDefIndexESConfiguration{
//        @Bean
//        fun createDefaultDefIndexES() = DefIndexES("prajuda.test")
//    }

    var embeddedES : EmbeddedElastic?=null

    var tcpPort = 9350

    var httpPort = 9351

    var clusterName = "test_cluster"

    val logger = loggerFor(ElasticSearchServer::class)

    @PostConstruct
    fun initElasticSearch() {
        logger.info("init ElasticSearch Server[httpPort={}, tcpPort={}]", httpPort, tcpPort)
//        val builder = EmbeddedElastic.builder()
//                .withElasticVersion("6.2.4")
//                .withDownloadDirectory(getSafeDownloadDirectory())
//                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, tcpPort)
//                .withSetting(PopularProperties.HTTP_PORT, httpPort)
//                .withSetting(PopularProperties.CLUSTER_NAME, clusterName)
//
//        applyIndexes(builder)
//
//        embeddedES = builder.build()
//        embeddedES?.start()
//        logger.info("Initied Elastic Search Server")
    }

    private fun getSafeDownloadDirectory(): File {
        val dir = File(downloadDir)

        if(!dir.parentFile.exists())
            dir.parentFile.mkdir()
        return dir
    }

    private fun applyIndexes(builder:EmbeddedElastic.Builder){
        indexes.forEach{
            builder.withIndex(it.name)
        }
    }

//    @EventListener(ContextStoppedEvent::class)
    @PreDestroy
    fun destroyElasticSearch() {
        logger.info("close ElasticSearch Server")
//        embeddedES?.let {
//            it.deleteIndices()
//            it.stop()
//        }

        logger.info("closed ElasticSearch Server")
    }

}