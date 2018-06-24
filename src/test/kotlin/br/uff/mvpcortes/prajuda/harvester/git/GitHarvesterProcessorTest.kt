package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.GitTestRepository
import br.uff.mvpcortes.prajuda.harvester.HarvesterProcessor
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import br.uff.mvpcortes.prajuda.modelService.PrajService
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderTestImpl
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a GitHarvesterProcessor ")
internal class GitHarvesterProcessorTest(){

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
    inner class ` references a git repository `{
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

            harvester.harvestComplete(service!!, harvestedList.consumer())

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

    }
}