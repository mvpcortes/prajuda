package br.uff.mvpcortes.prajuda.service.config

import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import com.github.vanroy.springboot.autoconfigure.data.jest.ElasticsearchJestAutoConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@ComponentScan(basePackageClasses = [ConfigService::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a ConfigService ")
class ConfigPrajServiceTest (
        @Autowired val  configService: ConfigService,
        @Autowired val workDirectoryProvider: WorkDirectoryProvider
){

    val idHarvester = "id_harvester"
    /**
     * Exclude  jest dependency
     */
    @TestConfiguration
    @SpringBootApplication(exclude = arrayOf(ElasticsearchJestAutoConfiguration::class))
    class MyConfiguration{

    }

    @Test
    fun ` the context is initied `(){

    }


    @Nested
    inner class ` get a havest directory ` {

        val harvestDir: File = configService.getWorkDirectoryForHarvester(idHarvester)

        @BeforeEach
        fun init(){
            harvestDir.tryDeleteRecursively()
            harvestDir.mkdir()
        }
        @AfterEach
        fun drop(){
            harvestDir.tryDeleteRecursively()
        }

        @Test
        fun `then the name is the harvester id `() {
            assertThat(harvestDir.name).isEqualTo(idHarvester)
        }

        @Test
        fun `then it exists `() {
            assertThat(harvestDir).exists()
        }

        @Test
        fun `then it is a directory `() {
            assertThat(harvestDir).isDirectory()
        }

        @Test
        fun `then the directory is a child of prajuda dir`() {
            assertThat(harvestDir.parentFile).isEqualTo(workDirectoryProvider.workDirectory())
        }


        @Nested
        inner class ` and get again the harvest directory ` {

            val harvestDir2: File = configService.getWorkDirectoryForHarvester(idHarvester)


            @Test
            fun `then the name is the harvester id `() {
                assertThat(harvestDir2.name).isEqualTo(idHarvester)
            }

            @Test
            fun `then it exists `() {
                assertThat(harvestDir2).exists()
            }

            @Test
            fun `then it is a directory `() {
                assertThat(harvestDir2).isDirectory()
            }

            @Test
            fun `then the directory is a child of prajuda dir`() {
                assertThat(harvestDir2.parentFile).isEqualTo(workDirectoryProvider.workDirectory())
            }

            @Test
            fun ` the file is equal to previous file `() {
                assertThat(harvestDir2).isEqualTo(harvestDir);
            }
        }
    }

    @Nested
    inner class ` the expected directory is a file ` {
        val previousFile = File(workDirectoryProvider.workDirectory(), idHarvester);
        init{
            previousFile.createNewFile()
            previousFile.writeText("xuxu")
        }

        @Test
        fun ` try create directory it will fail `(){
            val exception = assertThrows<IllegalStateException> {
                configService.getWorkDirectoryForHarvester(idHarvester)
            }
            assertThat(exception).hasMessage("Cannot create workdir for ${idHarvester}")
        }
    }
}