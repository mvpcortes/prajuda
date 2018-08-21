package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import io.github.bonigarcia.SeleniumExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate
import reactor.test.StepVerifier
import java.sql.ResultSet

@ExtendWith(value=[SpringExtension::class, SeleniumExtension::class])
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

}