package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate

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

}