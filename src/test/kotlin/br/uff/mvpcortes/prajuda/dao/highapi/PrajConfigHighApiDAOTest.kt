package br.uff.mvpcortes.prajuda.dao.highapi

import br.uff.mvpcortes.prajuda.service.config.DefIndexES
import br.uff.mvpcortes.prajuda.PrajudaApplication
import br.uff.mvpcortes.prajuda.dao.PrajConfigDAO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@DisplayName("When a PrajConfigHighApiDAO")
class PrajConfigHighApiDAOTest{

    @TestConfiguration
    class MyConfiguration{
        @Bean
        fun indexAdmin() = DefIndexES("prajuda.admin")
    }

    @Autowired
    lateinit var prajConfigDAO: PrajConfigDAO

    @BeforeEach
    fun init(){
        prajConfigDAO.initConfigIfNotExists()
    }

    @Test
    fun test(){
        val config = prajConfigDAO.get()

        assertThat(config.id).isNotNull()
        assertThat(config.id).isEqualTo(PrajConfigDAO.DEFAULT_ID)
    }

}