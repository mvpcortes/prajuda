package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import reactor.test.StepVerifier


@ExtendWith(SpringExtension::class)
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("A PrajServiceJDBCDAO")
class PrajServiceJDBCDAOTest{

    @Autowired
    private lateinit var prajServiceDAO: PrajServiceDAO


    @Autowired
    private lateinit var prajDocumentJDBCDAO: PrajDocumentDAO

    @Test
    fun `we can get they from database`(){
        prajServiceDAO.findIds()
    }

    fun assertNewId(prajService:PrajService, prajServiceSaved:PrajService) {
        assertThat(prajServiceSaved).isNotNull
        assertThat(prajServiceSaved.id).isEqualTo(prajServiceSaved.id)
        assertThat(prajServiceSaved).isSameAs(prajService)

        val prajServiceDB = prajServiceDAO.findByIdNullable(prajServiceSaved.id!!)!!


        assertThat(prajServiceDB.id).isNotNull()
        assertThat(prajServiceDB.id).isEqualTo(prajService.id)//not update id because
    }

    @Nested
    inner class `when a new PrajService with empty id is saved` {

        private val prajService: PrajService = PrajServiceFixture.withName("Test Service")

        private lateinit var prajServiceSaved: PrajService

        @BeforeEach
        private fun init() {
            prajService.id = ""
            prajServiceSaved = prajServiceDAO.save(prajService)
        }

        @AfterEach
        private fun drop() {
            prajServiceDAO.delete(prajServiceSaved.id!!)
        }

        @Test
        fun `then generate a new ID`() {
            assertNewId(prajService, prajServiceSaved)
        }
    }


    @Nested
    inner class `when a new PrajService is saved`{

        private val prajService : PrajService=PrajServiceFixture.withName("Test Service")

        private lateinit var prajServiceSaved:PrajService

        @BeforeEach
        private fun init(){
            prajServiceSaved = prajServiceDAO.save(prajService)
        }

        @AfterEach
        private fun drop(){
            prajServiceDAO.delete(prajServiceSaved.id!!)
        }

        @Test
        fun `then generate a new ID`() {
            assertNewId(prajService, prajServiceSaved)
        }

        @Test
        fun `then generate a data persisted`(){

            val prajServiceDB = prajServiceDAO.findByIdNullable(prajServiceSaved.id!!)!!

            SoftAssertions().let {
                it.assertThat(prajServiceDB.id).isEqualTo(prajService.id)
                it.assertThat(prajServiceDB.name).isEqualTo(prajService.name)
                it.assertThat(prajServiceDB.description).isEqualTo(prajService.description)
                it.assertThat(prajServiceDB.documentDir).isEqualTo(prajService.documentDir)
                it.assertThat(prajServiceDB.harvesterTypeId).isEqualTo(prajService.harvesterTypeId)
                it.assertThat(prajServiceDB.url).isEqualTo(prajService.url)
                it.assertThat(prajServiceDB.repositoryInfo.branch).isEqualTo(prajService.repositoryInfo.branch)
                it.assertThat(prajServiceDB.repositoryInfo.uri).isEqualTo(prajService.repositoryInfo.uri)
                it.assertThat(prajServiceDB.repositoryInfo.lastTag).isEqualTo(prajService.repositoryInfo.lastTag)
                it.assertThat(prajServiceDB.repositoryInfo.lastModified).isEqualToIgnoringSeconds(prajService.repositoryInfo.lastModified)
                it.assertThat(prajServiceDB.repositoryInfo.username).isEqualTo(prajService.repositoryInfo.username)
                it.assertThat(prajServiceDB.repositoryInfo.password).isEqualTo(prajService.repositoryInfo.password)
                it
            }.assertAll()

            assertThat(prajServiceDB).isNotSameAs(prajService)
        }

        @Nested
        inner class `and updated`{

            lateinit var prajServiceUpdated:PrajService

            @BeforeEach
            fun init(){
                prajServiceUpdated=prajServiceSaved.copy(name="New Name Service")
                prajServiceDAO.save(prajServiceUpdated)
            }

            @Test
            fun `then ID is equals`(){
                assertThat(prajServiceSaved.id).isEqualTo(prajServiceUpdated.id)
            }

            @Test
            fun `then name is updated`(){
                prajServiceDAO.findByIdNullable(prajServiceUpdated.id!!)
                        .let{it!!}
                        .let{ps->
                            SoftAssertions().let { softly ->
                                softly.assertThat(ps.name).isEqualTo(prajServiceUpdated.name)
                                softly.assertThat(ps.name).isNotEqualTo(prajServiceSaved.name)
                            softly
                            }.assertAll()
                        }
            }

            @Test
            fun `then other fields is not updated`(){
                prajServiceDAO.findByIdNullable(prajServiceUpdated.id!!)
                        .let{it!!}
                        .let{ps->
                            SoftAssertions().let { softly ->
                                softly.assertThat(ps.id).isEqualTo(prajServiceSaved.id)
                                softly.assertThat(ps.documentDir).isEqualTo(prajServiceSaved.documentDir)
                                softly.assertThat(ps.description).isEqualTo(prajServiceSaved.description)
                                softly.assertThat(ps.harvesterTypeId).isEqualTo(prajServiceSaved.harvesterTypeId)
                                softly.assertThat(ps.url).isEqualTo(prajServiceSaved.url)
                                softly.assertThat(ps.repositoryInfo.branch).isEqualTo(prajServiceSaved.repositoryInfo.branch)
                                softly.assertThat(ps.repositoryInfo.uri).isEqualTo(prajServiceSaved.repositoryInfo.uri)
                                softly.assertThat(ps.repositoryInfo.lastTag).isEqualTo(prajServiceSaved.repositoryInfo.lastTag)
                                softly.assertThat(ps.repositoryInfo.lastModified).isEqualTo(prajServiceSaved.repositoryInfo.lastModified)
                                softly.assertThat(ps.repositoryInfo.username).isEqualTo(prajServiceSaved.repositoryInfo.username)
                                softly.assertThat(ps.repositoryInfo.password).isEqualTo(prajServiceSaved.repositoryInfo.password)
                                softly
                            }.assertAll()
                        }
            }
        }

        @Test
        fun `and deleted then should not exists on database`(){
            drop()
            val prajServiceDeleted = prajServiceDAO.findByIdNullable(prajServiceSaved.id!!)

            assertThat(prajServiceDeleted).isNull()
        }

        @Nested
        inner class `and update tag`{

            @Test
            fun `then change in database`(){
                assertThat(prajServiceSaved.repositoryInfo.lastTag).isNull()

                prajServiceDAO.updateTag(prajServiceSaved.id!!, "XUXU XAXA")

                val prajServiceUpdated = prajServiceDAO.findByIdNullable(prajServiceSaved.id!!)!!

                assertThat(prajServiceUpdated.repositoryInfo.lastTag).isEqualTo("XUXU XAXA")
                assertThat(prajServiceUpdated.repositoryInfo.lastModified).isAfterOrEqualTo(prajServiceSaved.repositoryInfo.lastModified)
            }

            @Nested
            inner class `and has 3 documents`{

                val listDocuments:MutableList<PrajDocument> = mutableListOf()

                fun createDocument(id:Int,
                                   service:PrajService)= PrajDocument(
                        content = "CONTENT_$id",
                        tag = "TAG 3 DOC - $id",
                        path = "TAG/$id",
                        serviceId = prajService.id,
                        serviceName= prajService.name
                )

                @BeforeEach
                fun init(){
                    listDocuments.add(createDocument(1, prajServiceSaved))
                    listDocuments.add(createDocument(2, prajServiceSaved))
                    listDocuments.add(createDocument(3, prajServiceSaved))
                    listDocuments.forEach{prajDocumentJDBCDAO.save(it)}
                }

                @AfterEach
                fun drop(){
                    listDocuments.forEach{prajDocumentJDBCDAO.delete(it)}
                }

                @Test
                fun ` and update tag then update tag of 3 documents`(){
                    listDocuments.forEachIndexed { index, prajDocument ->
                        assertThat(prajDocument.tag).isEqualTo("TAG 3 DOC - ${index + 1}")
                    }
                    prajDocumentJDBCDAO.updateTag(prajServiceSaved.id!!, "NEW TAG DOC")

                    val listDocumentsSaved = prajDocumentJDBCDAO.findByService(prajServiceSaved.id!!)

                    listDocumentsSaved.forEach {
                        assertThat(it.tag).isEqualTo("NEW TAG DOC")
                    }
                }
            }

        }
    }

    @Test
    @Transactional
    @Rollback
    fun `when navigate by pagination and get first page then get first 10 services`() {
        prajServiceDAO.findIds().forEach{prajServiceDAO.delete(it)}
        val listPrajService =  (1..20).map { i ->
            prajServiceDAO.save(PrajServiceFixture.withName("pagination_$i"))
        }

        val flux = prajServiceDAO.findPage(0, 10).log()

        StepVerifier.create(flux)
                .expectNext(*(listPrajService.subList(0, 10).toTypedArray()))
                .verifyComplete()
    }

    @Test
    @Transactional
    @Rollback
    fun `when navigate by pagination and get second page then get last 10 services`() {
        prajServiceDAO.findIds().forEach{prajServiceDAO.delete(it)}
        val listPrajService =  (1..20).map { i ->
            prajServiceDAO.save(PrajServiceFixture.withName("pagination_$i"))
        }

        val flux = prajServiceDAO.findPage(1, 10).log()

        StepVerifier.create(flux)
                .expectNext(*(listPrajService.subList(10, 20).toTypedArray()))
                .verifyComplete()
    }

    @Test
    @Transactional
    @Rollback
    fun `when find by ids and exists one of ids then return them`() {
        prajServiceDAO.findIds().forEach{prajServiceDAO.delete(it)}
        val listPrajService =  (1..20).map { i ->
            prajServiceDAO.save(PrajServiceFixture.withName("pagination_$i"))
        }

        val flux = prajServiceDAO.findByIds("-1", "-2", "-3", listPrajService[0].id!! )

        StepVerifier.create(flux)
                .expectNext(listPrajService[0])
                .verifyComplete()
    }

    @Test
    @Transactional
    @Rollback
    fun `when count elements then return exact count`() {
        prajServiceDAO.findIds().forEach{prajServiceDAO.delete(it)}
        val listPrajService =  (1..20).map { i ->
            prajServiceDAO.save(PrajServiceFixture.withName("pagination_$i"))
        }

        assertThat(prajServiceDAO.count()).isEqualTo(listPrajService.size.toLong())

    }

}