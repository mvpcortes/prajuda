package br.uff.mvpcortes.prajuda.dao.highapi

import br.uff.mvpcortes.prajuda.dao.PrajConfigDAO
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.service.config.InitESService
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic
import util.ElasticSearchServerExtension

@ExtendWith(*[ElasticSearchServerExtension::class, SpringExtension::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("With ElasticSearch DB")
@SpringBootTest()
internal class ElasticSearchIntegrationTest(
        @Autowired val initESService: InitESService,
        @Autowired  val esHighApiProperties: ESHighApiProperties){


    @BeforeAll
    fun init(){
        initESService.initElasticSearch()
    }

    @ExtendWith(*[ElasticSearchServerExtension::class, SpringExtension::class])
    @SpringBootTest()
    @Nested
    inner class `when the prajuda config`(){

        @Autowired lateinit var prajConfigDAO: PrajConfigDAO


        @Test
        fun `then has the thefault id`() {
            val config = prajConfigDAO.get()

            assertThat(config.id).isNotNull()
            assertThat(config.id).isEqualTo(PrajConfigDAO.DEFAULT_ID)
        }


        @Test
        fun ` and call initConfig then will work and save config`(){
            prajConfigDAO.deleteConfig()
            prajConfigDAO.initConfigIfNotExists()

            //
            val config = prajConfigDAO.get()
            assertThat(config.id).isEqualTo(PrajConfigDAO.DEFAULT_ID)
            assertThat(config.name).isEqualTo("prajuda")
        }
    }

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

}