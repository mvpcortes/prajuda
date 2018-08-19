package br.uff.mvpcortes.prajuda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.support.ResourceBundleMessageSource

//@EnableScheduling
@SpringBootApplication
@Import(br.uff.mvpcortes.prajuda.dao.impl.jdbc.PrajudaJdbcConfiguration::class)
class PrajudaApplication{

}

fun main(args: Array<String>) {

    runApplication<PrajudaApplication>(*args)
}
