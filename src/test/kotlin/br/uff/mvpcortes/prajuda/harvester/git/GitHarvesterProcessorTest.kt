package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.GitTestRepository
import br.uff.mvpcortes.prajuda.harvester.HarvesterProcessor
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderTestImpl
import com.github.vanroy.springboot.autoconfigure.data.jest.ElasticsearchJestAutoConfiguration
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.TestConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a GitHarvesterProcessor ")
internal class GitHarvesterProcessorTest {

    /**
     * Exclude  jest dependency
     */
    @TestConfiguration
    @SpringBootApplication(exclude = [(ElasticsearchJestAutoConfiguration::class)])
    class MyConfiguration

    @Test
    fun `the context is initied `(){

    }

    val workDirectoryProvider= WorkDirectoryProviderTestImpl()

    val configService = ConfigService(workDirectoryProvider)

    val harvester = GitHarvesterProcessor(configService)

    @BeforeAll
    fun initAll(){
        workDirectoryProvider.onStartedEvent(mock())
    }

    @AfterAll
    fun dropAll(){
        workDirectoryProvider.onClosedEvent(mock())
    }


    @Nested
    inner class `references a git repository`{
        var gitTestRepository: GitTestRepository=GitTestRepository()

        val harvestedList = HarvestedConsumerList()

        var service = PrajService()

        @BeforeEach
        fun init(){
            gitTestRepository.close()
            harvestedList.clear()
            gitTestRepository = GitTestRepository()
            gitTestRepository.createRepository()
            service = PrajServiceFixture.withRepository(gitTestRepository.getUri())
        }

        @AfterEach
        fun drop(){
            gitTestRepository.close()
        }

        @Test
        fun `with one file then harvest only this file`(){
            gitTestRepository.changeMasterTo("1")

            harvester.harvestComplete(service, harvestedList.consumer())

            assertThat(harvestedList).hasSize(1)
            val havested = harvestedList[0]

            assertThat(havested.op).isEqualTo(HarvesterProcessor.HarvestedOp.UPDATED)
            assertThat(havested.doc.id).isEqualTo(null)
            assertThat(havested.doc.path).isEqualTo("main.md")
            assertThat(havested.doc.tag).isEqualTo("1")
            assertThat(havested.doc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(havested.doc.serviceName).isEqualTo("teste")
            assertThat(havested.doc.content).containsSubsequence(
                    "# Micat pectore decipis aliquisque bracchia quoque mando",
                    "## Per tantum",
                    "*Lorem* markdownum falsi, te plura Aeolidae volucrem dextrae herbis inmanem"
            )
        }

        @Test
        fun `with tree files on second commit then harvest these files`(){
            gitTestRepository.changeMasterTo("2")

            harvester.harvestComplete(service, harvestedList.consumer())

            assertThat(harvestedList).hasSize(2)

            assertThat(harvestedList[0].op).isEqualTo(HarvesterProcessor.HarvestedOp.UPDATED)
            assertThat(harvestedList[0].doc.id).isEqualTo(null)
            assertThat(harvestedList[0].doc.path).isEqualTo("main.md")
            assertThat(harvestedList[0].doc.tag).isEqualTo("2")
            assertThat(harvestedList[0].doc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(harvestedList[0].doc.serviceName).isEqualTo("teste")
            assertThat(harvestedList[0].doc.content).isEqualTo("xuxu xaxa")

            assertThat(harvestedList[1].op).isEqualTo(HarvesterProcessor.HarvestedOp.UPDATED)
            assertThat(harvestedList[1].doc.id).isEqualTo(null)
            assertThat(harvestedList[1].doc.path).isEqualTo("src/code.md")
            assertThat(harvestedList[1].doc.tag).isEqualTo("2")
            assertThat(harvestedList[1].doc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(harvestedList[1].doc.serviceName).isEqualTo("teste")
            assertThat(harvestedList[1].doc.content).isEqualTo(gitTestRepository.STR_CODE_MD)
        }

        @Test
        fun `with rename_file_on_third commit then harvest files`(){
            gitTestRepository.changeMasterTo("3")

            harvester.harvestComplete(service, harvestedList.consumer())

            assertThat(harvestedList).hasSize(4)

            val harvested = harvestedList.filter{it.doc.path == "org/main.md"}.map{it.doc}.single()

            assertThat(harvested.id).isEqualTo(null)
            assertThat(harvested.path).isEqualTo("org/main.md")//moved
            assertThat(harvested.tag).isEqualTo("3")
            assertThat(harvested.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(harvested.serviceName).isEqualTo("teste")
            assertThat(harvested.content).isEqualTo("xuxu xaxa")
        }

    }
}