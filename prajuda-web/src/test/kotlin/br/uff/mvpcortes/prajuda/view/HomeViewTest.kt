package br.uff.mvpcortes.prajuda.view

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajDocumentFixture
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import br.uff.mvpcortes.prajuda.service.RecommendationService
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.whenever
import io.github.bonigarcia.SeleniumExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.toFlux


@ExtendWith(value=[SpringExtension::class, SeleniumExtension::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeViewTest:AbstractViewTest(){

        @MockBean
        lateinit var recommendationService: RecommendationService

        @Autowired
        lateinit var serviceDAO: PrajServiceDAO

        @Autowired
        lateinit var documentDAO: PrajDocumentDAO

        @BeforeEach
        fun init(){
            doAnswer { listServices.toFlux() }.whenever(recommendationService).recommendServices()
        }

        val listServices = listOf(
                PrajService(id="1", name="name = 1"),
                PrajService(id="10", name="name = 10"),
                PrajService(id="50", name="name = 50"),
                PrajService(id="100", name="name = 100"),
                PrajService(id="150", name="name = 150"),
                PrajService(id="200", name="name = 200"),
                PrajService(id="250", name="name = 250"),
                PrajService(id="300", name="name = 300"),
                PrajService(id="350", name="name = 350")
        )


        @Test
        fun `when load index page then load services`(webDriver:HtmlUnitDriver){
            get(webDriver, "index.html")

            listServices.forEach { service->
                val spanService = webDriver.findElement(By.id(service.id+"_service_id"))
                val linkService = webDriver.findElement(By.id(service.id+"_service_name"))

                assertThat(spanService.tagName).isEqualTo("span")
                assertThat(spanService.text).isEqualTo("#${service.id}")

                assertThat(linkService.tagName).isEqualTo("a")
                assertThat(linkService.text).isEqualTo(service.name)
                assertThat(linkService.getAttribute("href")).endsWith("/service/${service.id}.html")
            }

            //count elements
            val qtd = webDriver.findElements(By.cssSelector(".service_column")).size
            assertThat(qtd).isEqualTo(listServices.size)

        }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `The document pages`{

        var prajService:PrajService = PrajServiceFixture.withName("service_to_document")

        var prajDocument: PrajDocument = PrajDocument()

        @BeforeAll
        fun initAll(){
            serviceDAO.save(prajService)
            prajDocument =  PrajDocumentFixture.new(prajService=prajService)
            documentDAO.save(prajDocument)
        }

        @AfterAll
        fun destroyAll(){
            documentDAO.delete(prajDocument!!)
            serviceDAO.delete(prajService.id!!)
        }

        @Test
        fun `when a document is found and showed then render HTML content`(webDriver:HtmlUnitDriver){
            val strPath = "document/${prajService.namePath}/${prajDocument.path}"
            get(webDriver, strPath)

            val htmlDocument = webDriver.findElement(By.id("document_${prajDocument.id}"))

            assertThat(htmlDocument.getAttribute("innerHTML")).containsSequence(PrajDocumentFixture.STR_VALID_MD_SIMPLE)
        }
    }
}