package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import reactor.test.StepVerifier

@ExtendWith(value=[SpringExtension::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ReactiveJdbcTemplateTest{

    @Autowired
    lateinit var jdbcTemplate:JdbcTemplate

    @Autowired
    lateinit var transactionTemplate: TransactionTemplate

    lateinit var reactiveJdbcTemplate:ReactiveJdbcTemplate

    @BeforeEach
    fun init(){
        reactiveJdbcTemplate = ReactiveJdbcTemplate(transactionTemplate, jdbcTemplate)
    }

    @Test
    @Transactional
    @Rollback
    fun `when query divided string window with window less than string size then return all string`(){
        jdbcTemplate.update("INSERT INTO entity_test VALUES (1, 'data')")

        val str = reactiveJdbcTemplate.queryStringWindow("SELECT data str FROM entity_test WHERE id = ? ", 0, 1,
                arrayOf(1))

        assertThat(str).isEqualTo("d")
    }

    @Test
    @Transactional
    @Rollback
    fun `when query divided string window with window equal_to string size then return all string`(){
        jdbcTemplate.update("INSERT INTO entity_test VALUES (1, 'data')")

        val str = reactiveJdbcTemplate.queryStringWindow("SELECT data str FROM entity_test WHERE id = ? ", 0, 4,
                arrayOf(1))

        assertThat(str).isEqualTo("data")
    }

    @Test
    @Transactional
    @Rollback
    fun `when query divided string window with window greater_to string size then return all string`(){
        jdbcTemplate.update("INSERT INTO entity_test VALUES (1, 'data')")

        val str = reactiveJdbcTemplate.queryStringWindow("SELECT data str FROM entity_test WHERE id = ? ", 0, 40,
                arrayOf(1))

        assertThat(str).isEqualTo("data")
    }

    @Test
    @Transactional
    @Rollback
    fun `when query string divided on flux then follow right way on flux`(){
        jdbcTemplate.update("INSERT INTO entity_test VALUES (1, 'on my way with a lot of spaces')")

        val flux = reactiveJdbcTemplate.queryStringDividedOnFlux(
                "SELECT data str FROM entity_test WHERE id = ?",
                        4L,
                        arrayOf<Any>(1))

        StepVerifier.create(flux)
                .expectNext("on m")
                .expectNext("y wa")
                .expectNext("y wi")
                .expectNext("th a")
                .expectNext(" lot")
                .expectNext(" of ")
                .expectNext("spac")
                .expectNext("es")
                .expectComplete()
    }

    @Test
    @Transactional
    @Rollback
    fun `when query string divided on flux and document not found then flux is empty`(){
        val flux = reactiveJdbcTemplate.queryStringDividedOnFlux(
                "SELECT data str FROM entity_test WHERE id = ?",
                4L,
                arrayOf<Any>(-5))

        StepVerifier.create(flux)
                .expectComplete()
    }


}