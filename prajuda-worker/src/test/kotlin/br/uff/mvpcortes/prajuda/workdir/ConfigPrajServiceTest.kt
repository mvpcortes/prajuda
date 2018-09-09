package br.uff.mvpcortes.prajuda.workdir

import br.uff.mvpcortes.prajuda.config.WorkerTestConfiguration
import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.File

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes=[WorkerTestConfiguration::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a WorkDirectoryService ")
class ConfigPrajServiceTest (
        @Autowired val configService: WorkDirectoryService,
        @Autowired val workDirectoryProvider: WorkDirectoryProvider){

    val idHarvester = "id_harvester"

//    /**
//     * Exclude  jest dependency
//     */
//    @TestConfiguration
//    @SpringBootApplication(exclude = [(ElasticsearchJestAutoConfiguration::class)])
//    class MyConfiguration

    @Test
    fun `the context is initialized `(){

    }


    @Nested
    inner class `get a havest directory `{

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
        inner class `and get again the harvest directory `{

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
            fun `the file is equal to previous file `() {
                assertThat(harvestDir2).isEqualTo(harvestDir)
            }
        }
    }

    @Nested
    inner class `the expected directory is a file `{
        val previousFile = File(workDirectoryProvider.workDirectory(), idHarvester)

        init{
            previousFile.createNewFile()
            previousFile.writeText("xuxu")
        }

        @Test
        fun `try create directory it will fail `(){
            val exception = assertThrows<IllegalStateException> {
                configService.getWorkDirectoryForHarvester(idHarvester)
            }
            assertThat(exception).hasMessage("Cannot createHelper workdir for $idHarvester")
        }
    }
}