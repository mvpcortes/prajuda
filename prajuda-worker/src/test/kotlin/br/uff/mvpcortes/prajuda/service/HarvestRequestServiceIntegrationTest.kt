package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.config.WorkerTestConfiguration
import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.git.GitTestRepository
import br.uff.mvpcortes.prajuda.model.HarvestRequest
import br.uff.mvpcortes.prajuda.model.fixture.HarvesterRequestFixture
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes=[WorkerTestConfiguration::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("A HarvestRequestService in integration")
class HarvestRequestServiceIntegrationTest{

    private val gitTestRepository = GitTestRepository()

    private var prajService = PrajServiceFixture.withRepository(gitTestRepository.getUri()).let{it.id = null;it}

    @Autowired
    lateinit var prajServiceDAO: PrajServiceDAO

    @Autowired
    lateinit var prajDocumentDAO: PrajDocumentDAO

    @Autowired
    lateinit var harvestRequestService: HarvestRequestService

    var harvestRequest:HarvestRequest?=null

    @BeforeEach
    fun init(){
        prajServiceDAO.save(prajService)
        harvestRequest = HarvesterRequestFixture.open(serviceId = prajService.id!!)
        harvestRequestService.harvestRequestDAO.save(harvestRequest!!)
        gitTestRepository.createRepository()
    }

    @AfterEach
    fun finish(){
        gitTestRepository.close()
        prajDocumentDAO.deleteByServiceId(prajService.id!!)
        prajServiceDAO.delete(prajService.id!!)
    }

    @Test
    fun `when try havester complete a empty git repository then create documents on DB`(){

        assertThat(prajService.repositoryInfo.lastTag).isNull()

        harvestRequestService.internalHarvesterWorker()

        val list = prajDocumentDAO.findByService(prajService.id!!).sortedBy { prajDocument -> prajDocument.path }

        assertThat(list).hasSize(3)

        list[0].let{
            assertThat(it.path).isEqualTo("org/main.md")
            assertThat(it.content).isEqualTo("xuxu xaxa")
            assertThat(it.serviceName).isEqualTo(prajService.name)
            assertThat(it.serviceId).isEqualTo(prajService.id)
            assertThat(it.tag).isEqualTo("4")
        }

        list[1].let{
            assertThat(it.path).isEqualTo("src/code.md")
            assertThat(it.content).containsSubsequence(
            "[Dicentem turres](http://nomine.com/prior): ille",
            "esports_namespace_trojan += scrollProtocol;",
            "terminal;")
            assertThat(it.serviceName).isEqualTo(prajService.name)
            assertThat(it.serviceId).isEqualTo(prajService.id)
            assertThat(it.tag).isEqualTo("4")
        }

        list[2].let{
            assertThat(it.path).isEqualTo("src/user.md")
            assertThat(it.content).isEqualTo("class user test content")
            assertThat(it.serviceName).isEqualTo(prajService.name)
            assertThat(it.serviceId).isEqualTo(prajService.id)
            assertThat(it.tag).isEqualTo("4")
        }

        val prajServiceSaved = prajServiceDAO.findByIdNullable(prajService.id!!)!!

        assertThat(prajServiceSaved.repositoryInfo.lastTag).isEqualTo("4")
        assertThat(prajServiceSaved.repositoryInfo.lastModified).isAfterOrEqualTo(prajService.repositoryInfo.lastModified)
    }


}