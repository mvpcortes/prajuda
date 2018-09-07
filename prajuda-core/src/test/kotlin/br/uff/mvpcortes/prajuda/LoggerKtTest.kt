package br.uff.mvpcortes.prajuda

import org.junit.jupiter.api.Test

internal class LoggerKtTest{

    @Test
    fun verify_test_was_created(){
        val logger = loggerFor(LoggerKtTest::class)
        logger.info("OK")
    }

    @Test
    fun verify_test_was_created_using_javaClass(){
        val logger = loggerFor(LoggerKtTest::class.java)
        logger.info("OK")
    }
}


