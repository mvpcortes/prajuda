package br.uff.mvpcortes.prajuda

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

//@EnableScheduling
@SpringBootApplication
@Import(br.uff.mvpcortes.prajuda.dao.impl.jdbc.PrajudaJdbcConfiguration::class)
class PrajudaApplication

fun main(args: Array<String>) {

//    @Bean
//    LayoutDialect layoutDialect() {
//        new LayoutDialect()
//    }
    runApplication<PrajudaApplication>(*args)
}
