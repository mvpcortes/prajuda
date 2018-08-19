package br.uff.mvpcortes.prajuda.view

import br.uff.mvpcortes.prajuda.api.FakeApi
import br.uff.mvpcortes.prajuda.loggerFor
import io.github.bonigarcia.SeleniumExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


@ExtendWith(value=[SpringExtension::class, SeleniumExtension::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ViewTest{

    @LocalServerPort
    var port:Int = 0

    val logger = loggerFor(ViewTest::class)

    val STR_REGEX_FAKE_URL = "^http\\:\\/\\/localhost\\:\\d+\\/fake\\/\\d+\\.html\$"


//    @MockBean
//    var prajServiceDAO: PrajServiceDAO? = null

    fun get(webDriver: WebDriver, path:String):WebDriver{

        //if is htmlUnit, force JS and download images
        if(webDriver is HtmlUnitDriver){
            webDriver.isJavascriptEnabled = true
            webDriver.isDownloadImages = true
         }


        //resize window to mobile form (mobile-first
        webDriver.manage().window().size = Dimension(360, 640)
        webDriver.get("http://localhost:$port/$path")

        //implicit wait
        webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)
        return webDriver
    }

//    @Nested
//    inner class `The main page`{
//
//
//        val listServices = listOf(
//                PrajService(id="001", name="name = 001"),
//                PrajService(id="010", name="name = 010"),
//                PrajService(id="050", name="name = 050"),
//                PrajService(id="100", name="name = 100"),
//                PrajService(id="150", name="name = 150"),
//                PrajService(id="200", name="name = 200"),
//                PrajService(id="250", name="name = 250"),
//                PrajService(id="300", name="name = 300"),
//                PrajService(id="350", name="name = 350")
//        )
//
//
//        @Test
//        fun `when load index page then load services`(webDriver:HtmlUnitDriver){
//            get(webDriver, "index.html")
//
//            listServices.forEach { service->
//                val spanService = webDriver.findElement(By.id(service.id+"_service_id"))
//                val linkService = webDriver.findElement(By.id(service.id+"_service_name"))
//
//                assertThat(spanService.tagName).isEqualTo("span")
//                assertThat(spanService.text).isEqualTo("#${service.id} ")
//
//                assertThat(linkService.tagName).isEqualTo("a")
//                assertThat(linkService.text).isEqualTo(service.name)
//                assertThat(linkService.getAttribute("href")).isEqualTo("/service/${service.id}.html")
//            }
//
//            //count elements
//            val qtd = webDriver.findElements(By.cssSelector(".service_column")).size
//            assertThat(qtd).isEqualTo(listServices.size)
//
//        }
//    }

    @Nested
    inner class `The create or edit service pages`{

        fun fillForm(webDriver: WebDriver, url:String = "https://my.app.io/test"){
            webDriver.findElement<WebElement>(By.id("name")).let{it.clear(); it.sendKeys("my service test")}
            webDriver.findElement<WebElement>(By.id("url")).let{it.clear(); it.sendKeys(url)}
            webDriver.findElement<WebElement>(By.id("description")).let{it.clear(); it.sendKeys("service description")}
            webDriver.findElement<WebElement>(By.id("documentDir")).let{it.clear(); it.sendKeys("prajuda")}
        }

        @Test
        fun `when save new service valid then show service data`(webDriver:HtmlUnitDriver){
            get(webDriver, "service/new.html")

            fillForm(webDriver)

            webDriver.findElement(By.id("submit_btn")).click()

            val wait = WebDriverWait(webDriver, 2);
            wait.until(ExpectedConditions.urlMatches(".*/service/(\\d+)\\.html"))

            assertThat(webDriver.findElement(By.id("name")).text).isEqualTo("my service test")
            assertThat(webDriver.findElement(By.id("description")).text).isEqualTo("service description")
            assertThat(webDriver.findElement(By.id("url")).text).isEqualTo("https://my.app.io/test")
            assertThat(webDriver.findElement(By.id("documentDir")).text).isEqualTo("prajuda")
            assertThat(webDriver.findElement(By.id("harvesterType")).text).isEqualTo("Git (Classic)")
        }

        @Test
        fun `when save new service with invalid url then show error message`(webDriver:HtmlUnitDriver){
            get(webDriver, "service/new.html")

            fillForm(webDriver = webDriver, url = "wrong url")

            webDriver.findElement(By.id("submit_btn")).click()

            println(webDriver.pageSource)

            val itemError = webDriver.findElement(By.xpath("//p[@data-error-for='url' and contains(@class, 'ajax-form-error-showed')]"))

            assertThat(itemError.text).isEqualTo("Is not a valid URL")
            assertThat(itemError.isDisplayed).isTrue()

            val wait = WebDriverWait(webDriver, 2);
            wait.until(ExpectedConditions.urlMatches(".*/service/new.html"))
        }
    }

    @Nested
    inner class `The form ajax submit`{

        @Test
        fun `when get fakedata then render fields`(webDriver:HtmlUnitDriver) {
            get(webDriver, "fake/243.html")

            assertThat(webDriver.findElement(By.id("fieldId")).text).isEqualTo("243")
            assertThat(webDriver.findElement(By.id("fieldString")).text).isEqualTo("string 243")
            assertThat(webDriver.findElement(By.id("fieldNumber")).text).isEqualTo("343")
            assertThat(webDriver.findElement(By.id("fieldDate")).text).isEqualTo("2018-09-01")
        }

        fun fillForm(webDriver: WebDriver, string:String="xuxu_xaxa", number:Long = 222, date:LocalDate = LocalDate.now()){
            webDriver.findElement<WebElement>(By.id("frm_default_field_string")).let{it.clear(); it.sendKeys(string.toString())}
            webDriver.findElement<WebElement>(By.id("frm_default_field_number")).let{it.clear(); it.sendKeys(number.toString())}
            webDriver.findElement<WebElement>(By.id("frm_default_field_date"))
                    .let{it.clear(); it.sendKeys(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))}
        }

        @Test
        fun `when submit valid fake data then redirect to fakedata page`(webDriver:HtmlUnitDriver) {
            get(webDriver, "fake/new.html")

            fillForm(webDriver)


            webDriver.findElement(By.id("frm_default_submit")).submit()

            val wait = WebDriverWait(webDriver, 2);
            wait.until(ExpectedConditions.urlMatches("^http\\:\\/\\/localhost\\:\\d+\\/fake\\/\\d+\\.html\$"))

            val id = webDriver.findElement(By.id("fieldId")).text.toLong()
            val fake = FakeApi.FakeData.withId(id);
            assertThat(webDriver.findElement(By.id("fieldString")).text).isEqualTo(fake.fieldString)
            assertThat(webDriver.findElement(By.id("fieldNumber")).text).isEqualTo(fake.fieldNumber.toString())
            assertThat(webDriver.findElement(By.id("fieldDate")).text).isEqualTo(fake.getStrDate())
        }

        @Test
        fun `when submit invalid fake data then show errors`(webDriver: HtmlUnitDriver){
            get(webDriver, "fake/new.html")

            fillForm(webDriver=webDriver, number = -1)

            webDriver.findElement(By.id("frm_default_submit")).submit()

            val item = webDriver.findElement(By.xpath("//span[@data-error-for='fieldNumber' and @class='ajax-form-error-showed']"))
            assertThat(item.getAttribute("id")).isEqualTo("xptoNumber")
            assertThat(item.tagName).isEqualTo("span")
            assertThat(item.getAttribute("class")).isEqualTo("ajax-form-error-showed")
            assertThat(item.text).isEqualTo("deve ser maior ou igual a 0")
            assertThat(item.isDisplayed).isTrue()
        }
    }

    @Test
    fun `verify regex for fake url`(){
        assertThat("http://localhost:4455/fake/1111.html").containsPattern(STR_REGEX_FAKE_URL)
    }

    @Test
    fun `when open main page then load title`(webDriver:HtmlUnitDriver) {

        get(webDriver, "/index.html")

        webDriver.findElementById("prajuda_logo").let{
            assertThat(it.isEnabled).isTrue()
            assertThat(it.tagName).isEqualTo("img")
            assertThat(it.getAttribute("alt")).isEqualTo("Prajuda logo")
        }
    }

    @Test
    fun `when click on open menu then show itens`(webDriver: HtmlUnitDriver){
        get(webDriver, "/index.html")


        val navMenu = webDriver.findElement(By.id("navMenuMore"))

        assertThat(navMenu.isEnabled).isTrue()
        assertThat(navMenu.isDisplayed).isFalse()



        webDriver.findElementById("navbar_burger").let {
            assertThat(it.isDisplayed).isTrue()
            it.click()
        }

        assertThat(navMenu.isEnabled).isTrue()
        assertThat(navMenu.isDisplayed).isTrue()

    }

    @Test
    fun `when click on close menu then hide itens`(webDriver: HtmlUnitDriver){
        get(webDriver, "/index.html")

        webDriver.findElementById("navbar_burger").let {
            assertThat(it.isDisplayed).isTrue()
            it.click()
        }

        val navMenu = webDriver.findElement(By.id("navMenuMore"))
        assertThat(navMenu.isDisplayed).isTrue()

        webDriver.findElementById("navbar_burger").let {
            assertThat(it.isDisplayed).isTrue()
            it.click()
        }

        assertThat(navMenu.isDisplayed).isFalse()

    }
}