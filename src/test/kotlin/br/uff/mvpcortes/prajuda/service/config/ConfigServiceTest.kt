package br.uff.mvpcortes.prajuda.service.config

import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ConfigServiceTest {

    val workDirectoryProvider=mock<WorkDirectoryProvider>{}

    var configService:ConfigService = ConfigService(workDirectoryProvider)
    @BeforeEach
    fun init(){
        Mockito.reset(workDirectoryProvider)
    }

    @Test
    fun getWorkDirectoryForHarvester() {

        val file = configService.getWorkDirectoryForHarvester("xuxu")
    }
}