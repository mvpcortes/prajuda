package br.uff.mvpcortes.prajuda.harvester.consumer

import br.uff.mvpcortes.prajuda.config.WorkerTestConfiguration
import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedFixture
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.model.RepositoryInfo
import br.uff.mvpcortes.prajuda.model.fixture.PrajDocumentFixture
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes=[WorkerTestConfiguration::class])
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("The HarvestedConsumerToDB in integration")
class HarvestedConsumerToDBIntegrationTest{

    @SpyBean
    lateinit var prajDocumentDAO: PrajDocumentDAO

    @Autowired
    lateinit var prajServiceDAO: PrajServiceDAO

    @Autowired
    lateinit var harvestedConsumerToDB: HarvestedConsumerToDB

    var prajService = PrajServiceFixture.withName("service_for_consumer_to_db_test")

    @BeforeEach
    fun init(){
        prajService = PrajServiceFixture.withName("service_for_consumer_to_db_test")
        prajServiceDAO.save(prajService)
    }

    @AfterEach
    fun destroy(){
        prajService.id?.let {
            prajDocumentDAO.deleteByServiceId(it)
            prajServiceDAO.delete(it)
        }
    }

    @Transactional
    @Rollback
    @Test
    fun `when document not exists in DB and try run noop then do nothing`() {
        prajDocumentDAO.deleteAll()

        reset(prajDocumentDAO)

        val harvested = HarvestedFixture.noop(id = 1)

        harvestedConsumerToDB.accept(harvested)

        inOrder(prajDocumentDAO) {
//            verify(prajDocumentDAO, times(1)).deleteAll()
            verifyNoMoreInteractions()
        }

        val qtd = prajDocumentDAO.count()

        assertThat(qtd).isEqualTo(0)

    }


    @Transactional
    @Rollback
    @Test
    fun `when document not exists in DB and try run update then call database but change nothing`() {
        prajDocumentDAO.deleteAll()

        reset(prajDocumentDAO)

        val harvested = HarvestedFixture.update(id = 1, serviceId = prajService.id!!)

        harvestedConsumerToDB.accept(harvested)


        verify(prajDocumentDAO, times(1)).saveTrackingServiceAndPath(harvested.doc)
        verify(prajDocumentDAO, never()).deleteTrackingServiceAndPath(harvested.doc)

        val qtd = prajDocumentDAO.count()

        assertThat(qtd).isEqualTo(1L)

    }

    @Transactional
    @Rollback
    @Test
    fun `when document not exists in DB and try run delete then call database but change nothing`() {
        prajDocumentDAO.deleteAll()

        reset(prajDocumentDAO)

        val harvested = HarvestedFixture.delete(id = 1, serviceId = prajService.id!!)

        harvestedConsumerToDB.accept(harvested)


        verify(prajDocumentDAO, times(1)).deleteTrackingServiceAndPath(harvested.doc)
        verify(prajDocumentDAO, never()).saveTrackingServiceAndPath(harvested.doc)

        val qtd = prajDocumentDAO.count()

        assertThat(qtd).isEqualTo(0)
    }

    @Transactional
    @Rollback
    @Test
    fun `when document exists document in DB and try run noop then do nothing`() {
        prajDocumentDAO.deleteAll()

        val harvested = HarvestedFixture.noop(id = null, serviceId = prajService.id!!)

        prajDocumentDAO.save(harvested.doc)

        assertThat(prajDocumentDAO.count()).isEqualTo(1)

        reset(prajDocumentDAO)

        harvestedConsumerToDB.accept(harvested)

        inOrder(prajDocumentDAO) {
            //            verify(prajDocumentDAO, times(1)).deleteAll()
            verifyNoMoreInteractions()
        }

        assertThat(prajDocumentDAO.count()).isEqualTo(1)

    }

    @Transactional
    @Rollback
    @Test
    fun `when document exists document in DB and try run update then update values`() {
        prajDocumentDAO.deleteAll()

        val prajServiceSaved = prajService.copy(repositoryInfo = RepositoryInfo(lastTag = "tag before", lastModified = LocalDateTime.now()))

        prajServiceDAO.save(prajServiceSaved)

        val prajDocument = PrajDocumentFixture
                .default(id=null, tag="tag before",
                        serviceId = prajServiceSaved.id!!,
                        serviceName = prajServiceSaved.name)

        prajDocumentDAO.save(prajDocument)

        assertThat(prajServiceSaved.repositoryInfo.lastTag).isEqualTo("tag before")
        assertThat(prajDocumentDAO.count()).isEqualTo(1)
        reset(prajDocumentDAO)

        val harvested = Harvested(op=HarvestedOp.UPDATED, doc=PrajDocumentFixture
                .default(
                    id=prajDocument.id,
                        tag = "tag after",
                        serviceId = prajServiceSaved.id!!,
                        serviceName = prajServiceSaved.name,
                        path=prajDocument.path
                )
        )

        harvestedConsumerToDB.accept(harvested)


        verify(prajDocumentDAO, times(1)).saveTrackingServiceAndPath(harvested.doc)
        verify(prajDocumentDAO, never()).deleteTrackingServiceAndPath(harvested.doc)

        assertThat(prajDocumentDAO.count()).isEqualTo(1)

        val prajServiceUpdated = prajServiceDAO.findByIdNullable(prajServiceSaved.id!!)!!
        val prajDocumentUpdated = prajDocumentDAO.findById(prajDocument.id!!)!!

        assertThat(prajServiceUpdated.repositoryInfo.lastTag).isEqualTo("tag before")// not shange in this time.

        assertThat(prajDocumentUpdated.tag).isEqualTo("tag after")

    }

    @Transactional
    @Rollback
    @Test
    fun `when document exists document in DB and try run delete then delete document`() {
        prajDocumentDAO.deleteAll()

        val prajServiceSaved = prajService.copy(repositoryInfo = RepositoryInfo(lastTag = "tag before", lastModified = LocalDateTime.now()))
        prajServiceDAO.save(prajServiceSaved)

        val prajDocument = PrajDocumentFixture.default(id=null, tag="tag before", serviceId = prajServiceSaved.id!!, serviceName = prajServiceSaved.name)
        prajDocumentDAO.save(prajDocument)

        assertThat(prajServiceSaved.repositoryInfo.lastTag).isEqualTo("tag before")
        assertThat(prajDocumentDAO.count()).isEqualTo(1)
        reset(prajDocumentDAO)

        val harvested = Harvested(op=HarvestedOp.DELETED, doc=PrajDocumentFixture
                .default(
                        id=prajDocument.id,
                        serviceId = prajServiceSaved.id!!,
                        serviceName = prajServiceSaved.name,
                        path=prajDocument.path
                )
        )

        harvestedConsumerToDB.accept(harvested)


        verify(prajDocumentDAO, never()).saveTrackingServiceAndPath(harvested.doc)
        verify(prajDocumentDAO, times(1)).deleteTrackingServiceAndPath(harvested.doc)

        assertThat(prajDocumentDAO.count()).isEqualTo(0)

        val prajServiceUpdated = prajServiceDAO.findByIdNullable(prajServiceSaved.id!!)!!
        val prajDocumentUpdated = prajDocumentDAO.findById(prajDocument.id!!)

        assertThat(prajServiceUpdated.repositoryInfo.lastTag).isEqualTo("tag before")// not shange in this time.

        assertThat(prajDocumentUpdated).isNull()

    }

}