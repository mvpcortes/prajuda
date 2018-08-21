package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("A PrajServiceJDBCDAO")
class PrajServiceJDBCDAOTest{

    @Autowired
    private lateinit var prajServiceDAO: PrajServiceDAO

    @Test
    fun `we can get they from database`(){
        prajServiceDAO.findIds()
    }


    @Nested
    inner class `when a new PrajService is saved`{

        private val prajService : PrajService=PrajServiceFixture.withName("Test Service")

        private lateinit var prajServiceSaved:PrajService

        @BeforeEach
        private fun init(){
            prajServiceSaved = prajServiceDAO.save(prajService)
        }

        @Test
        fun `then generate a new ID`(){

            assertThat(prajServiceSaved).isNotNull
            assertThat(prajServiceSaved.id).isEqualTo(prajServiceSaved.id)
            assertThat(prajServiceSaved).isSameAs(prajService)

            val prajServiceDB = prajServiceDAO.findByIdNullable(prajServiceSaved.id!!)!!


            assertThat(prajServiceDB.id).isNotNull()
            assertThat(prajServiceDB.id).isEqualTo(prajService.id)//not update id because
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
    }

 }