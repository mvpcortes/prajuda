package br.uff.mvpcortes.prajuda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

//@EnableElasticsearchRepositories(basePackageClasses = arrayOf(ConfigDAO::class))
@SpringBootApplication(exclude = [(ElasticsearchAutoConfiguration::class), (ElasticsearchDataAutoConfiguration::class)])
//@EnableScheduling
class PrajudaApplication

fun main(args: Array<String>) {
    runApplication<PrajudaApplication>(*args)
}
