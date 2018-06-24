package br.uff.mvpcortes.prajuda.service.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File

@ExtendWith(SpringExtension::class)
//@SpringBootTest
//@ComponentScan(basePackageClasses = [ConfigService::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a ConfigService ")
class ConfigServiceTesteOld {


    @Autowired
    lateinit var  configService: ConfigService
    @Autowired
    lateinit var workDirectoryProvider: WorkDirectoryProvider


    inner class ` get a havest directory `{

        val harvestDir: File = configService.getWorkDirectoryForHarvester("harvester_id")

        @Test
        fun `then it exists `(){
            assertThat(harvestDir).exists()
        }

        @Test
        fun `then it is a directory `(){
            assertThat(harvestDir).isDirectory()
        }

        @Test
        fun `then the directory is a child of prajuda dir`(){
            assertThat(harvestDir.parentFile).isEqualTo(workDirectoryProvider.workDirectory())
        }
    }

}