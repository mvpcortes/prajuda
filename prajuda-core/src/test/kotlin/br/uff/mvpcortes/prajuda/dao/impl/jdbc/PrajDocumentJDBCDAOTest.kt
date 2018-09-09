package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajDocumentFixture
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrajDocumentJDBCDAOTest{

    @Autowired
    lateinit var prajServiceJDBCDAO: PrajServiceJDBCDAO

    @Autowired
    lateinit var prajDocumentJDBCDAO: PrajDocumentJDBCDAO

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `when save a document`{

        var prajService : PrajService? = null

        @BeforeAll
        fun init(){
            prajService =  PrajServiceFixture.withName("XUXU")
            prajServiceJDBCDAO.save(prajService!!)

        }

        @AfterAll
        fun drop(){
            prajDocumentJDBCDAO.deleteByServiceId(prajService!!.id!!)
            prajServiceJDBCDAO.delete(prajService!!.id!!)
        }

        @Test
        fun `when try save a new document with id blank then create new id`(){
            val prajDocumentBlank :PrajDocument= PrajDocumentFixture.new(prajService=prajService!!)
            prajDocumentBlank.id = "    "
            prajDocumentJDBCDAO.save(prajDocumentBlank)
        }

        @Test
        fun `when update a document then ok`(){
            val prajDocument = PrajDocumentFixture.new(prajService = prajService!!)
            prajDocumentJDBCDAO.save(prajDocument)

            val prajDocumentChanged = prajDocument.copy(content="changed", path="a/b/c")

            prajDocumentJDBCDAO.save(prajDocumentChanged)

            val prajDocumentSaved = prajDocumentJDBCDAO.findById(prajDocument.id!!)!!

            assertThat(prajDocumentChanged.id).isEqualTo(prajDocumentSaved.id)
            assertThat(prajDocumentChanged.serviceId).isEqualTo(prajDocumentSaved.serviceId)
            assertThat(prajDocumentChanged.serviceName).isEqualTo(prajDocumentSaved.serviceName)
            assertThat(prajDocument.path).isNotEqualTo(prajDocumentSaved.path)
            assertThat(prajDocument.content).isNotEqualTo(prajDocumentSaved.content)
        }
    }
}