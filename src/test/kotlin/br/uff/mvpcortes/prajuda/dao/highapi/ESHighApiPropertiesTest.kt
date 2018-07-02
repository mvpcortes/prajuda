package br.uff.mvpcortes.prajuda.dao.highapi

import br.uff.mvpcortes.prajuda.dao.PrajConfigDAO
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic
import util.ElasticSearchServerExtension

@ExtendWith(*[ElasticSearchServerExtension::class, SpringExtension::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a ESHighApiProperties on spring-boot")
@SpringBootTest()
internal class ESHighApiPropertiesTest(@Autowired val prajConfigDAO: PrajConfigDAO, @Autowired  val esHighApiProperties: ESHighApiProperties, @Autowired prajConfigService: ConfigService){


    @Test
    fun `then port is equal to elasticSearchServer`(ee: EmbeddedElastic){
        Assertions.assertThat(esHighApiProperties.listHosts()[0].port).isEqualTo(ee.httpPort)
    }


    @Test
    fun `then has one connection to ES`(){
        Assertions.assertThat(esHighApiProperties.listHosts()).hasSize(1)
    }

    @Test
    fun `then the connection to ES is valid`(){
        assertThat(esHighApiProperties.listHosts()[0].host).isEqualTo("localhost")
        assertThat(esHighApiProperties.listHosts()[0].port).isEqualTo(9320)
    }

    @Test
    fun `and initConfig then will work and save config`(){
        prajConfigDAO.deleteConfig()
        prajConfigDAO.initConfigIfNotExists()

        //
        val config = prajConfigDAO.get()
        assertThat(config.id).isEqualTo(PrajConfigDAO.DEFAULT_ID)
        assertThat(config.name).isEqualTo("prajuda")
    }
}