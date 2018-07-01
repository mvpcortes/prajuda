package br.uff.mvpcortes.prajuda.harvester.orchestrator

import br.uff.mvpcortes.prajuda.GitTestRepository
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.git.GitFluxHarvesterProcessor
import br.uff.mvpcortes.prajuda.harvester.git.HarvestedConsumerList
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderTestImpl
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a HarvesterOrchestrator integr with GitHarvester")
class HarvesterOrchestratorIntegrGitTest{

    private val workDirectoryProvider= WorkDirectoryProviderTestImpl()

    private val configService = ConfigService(workDirectoryProvider)

    private val consumer=HarvestedConsumerList()

    private val prajServiceDAO:PrajServiceDAO = mock()

    private val gitFluxHarvesterProcessor=GitFluxHarvesterProcessor(configService)

    private val orchestrator = HarvesterOrchestrator(gitFluxHarvesterProcessor, consumer.javaConsumer(), prajServiceDAO)


    @BeforeEach
    fun init(){
        workDirectoryProvider.onStartedEvent(mock())

    }

    @AfterEach
    fun drop(){
        workDirectoryProvider.onClosedEvent(mock())
        Mockito.reset(prajServiceDAO)
    }


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `with one service`{

        private val gitTestRepository = GitTestRepository()

        private var prajService = PrajServiceFixture.withRepository(gitTestRepository.getUri())

        @BeforeEach
        fun init(){
            doReturn(listOf<String>(prajService.id!!) ).whenever(prajServiceDAO).findIds()
            doReturn(Optional.of(prajService)).whenever(prajServiceDAO).findById(prajService.id!!)
        }
        @BeforeAll
        fun initAll(){
            gitTestRepository.createRepository()
        }

        @AfterAll
        fun dropAll(){
            gitTestRepository.close()
        }

        @Test
        fun `and does complete harvester then copy all repository to consumer`(){
            orchestrator.harvesterComplete()

            consumer.assertFourthCommit()
        }

        @Test
        fun `and does diff harvester between commit 2 and commit 4 then copy all repository to consumer`(){
            gitTestRepository.changeMasterTo("2")
            orchestrator.harvesterComplete()

            prajService = PrajServiceFixture.withRepositoryAndTag(gitTestRepository.getUri(), "2")                //change tag of service
            consumer.clear()

            gitTestRepository.changeMasterTo("4")
            doReturn(Optional.of(prajService)).whenever(prajServiceDAO).findById(prajService.id!!)//force update prajService
            orchestrator.harvesterDiff()

            consumer.assertDiffSecondToFouthCommit()
        }
    }


}