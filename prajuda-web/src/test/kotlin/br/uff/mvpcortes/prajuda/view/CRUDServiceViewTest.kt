package br.uff.mvpcortes.prajuda.view

import br.uff.mvpcortes.prajuda.api.FakeApi
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajService
import io.github.bonigarcia.SeleniumExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@ExtendWith(value=[SpringExtension::class, SeleniumExtension::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CRUDServiceViewTest:AbstractViewTest(){



    companion object {
        private val logger = loggerFor(CRUDServiceViewTest::class)
        const val STR_REGEX_FAKE_URL = "^http\\:\\/\\/localhost\\:\\d+\\/fake\\/\\d+\\.html\$"
    }

    @Nested
    inner class `The create or edit service pages`{

        fun fillForm(webDriver: WebDriver, name:String = "my service test" , url:String = "https://my.app.io/test"){
            webDriver.findElement<WebElement>(By.id("name")).let{it.clear(); it.sendKeys(name)}
            webDriver.findElement<WebElement>(By.id("url")).let{it.clear(); it.sendKeys(url)}
            webDriver.findElement<WebElement>(By.id("description")).let{it.clear(); it.sendKeys("service description")}
            webDriver.findElement<WebElement>(By.id("documentDir")).let{it.clear(); it.sendKeys("prajuda")}
            webDriver.findElement<WebElement>(By.id("repositoryInfo.uri")).let{it.clear(); it.sendKeys("https://my.app.io/repository.git")}
            webDriver.findElement<WebElement>(By.id("repositoryInfo.branch")).let{it.clear(); it.sendKeys("master")}
            webDriver.findElement<WebElement>(By.id("repositoryInfo.username")).let{it.clear(); it.sendKeys("user")}
            webDriver.findElement<WebElement>(By.id("repositoryInfo.password")).let{it.clear(); it.sendKeys("1234")}
        }

        @Test
        fun `when save new service valid then show service data`(webDriver:HtmlUnitDriver){
            get(webDriver, "service/new.html")

            fillForm(webDriver)


            webDriver.findElement(By.id("submit_btn")).click()

            val wait = WebDriverWait(webDriver, 2)
            wait.until(ExpectedConditions.urlMatches(".*/service/(\\d+)\\.html"))

            assertServiceShow(webDriver)
        }

        private fun assertServiceShow(webDriver: HtmlUnitDriver, name:String="my service test") {
            assertThat(webDriver.findElement(By.id("name")).text).isEqualTo(name)
            assertThat(webDriver.findElement(By.id("namePath")).text).isEqualTo(PrajService.sanitizeName(name))
            assertThat(webDriver.findElement(By.id("description")).text).isEqualTo("service description")
            assertThat(webDriver.findElement(By.id("url")).text).isEqualTo("https://my.app.io/test")
            assertThat(webDriver.findElement(By.id("documentDir")).text).isEqualTo("prajuda")
            assertThat(webDriver.findElement(By.id("harvesterType")).text).isEqualTo("Git (Classic)")

            assertThat(webDriver.findElement(By.id("repositoryInfo_uri")).text).isEqualTo("https://my.app.io/repository.git")
            assertThat(webDriver.findElement(By.id("repositoryInfo_branch")).text).isEqualTo("master")
            assertThat(webDriver.findElement(By.id("repositoryInfo_username")).text).isEqualTo("user")
            assertThat(webDriver.findElement(By.id("repositoryInfo_password")).text).isEqualTo("")
            assertThat(webDriver.findElement(By.id("repositoryInfo_password")).getAttribute("value")).isEqualTo("1234")
            assertThat(webDriver.findElement(By.id("repositoryInfo_password_button")).text).isEqualTo("Show")
        }

        @Test
        fun `when save new service with invalid url then show error message`(webDriver:HtmlUnitDriver){
            get(webDriver, "service/new.html")

            fillForm(webDriver = webDriver, url = "wrong url")

            webDriver.findElement(By.id("submit_btn")).click()

            val itemError = webDriver.findElement(By.xpath("//p[@data-error-for='url' and contains(@class, 'ajax-form-error-showed')]"))

            assertThat(itemError.text).isEqualTo("Is not a valid URL")
            assertThat(itemError.isDisplayed).isTrue()

            val wait = WebDriverWait(webDriver, 2)
            wait.until(ExpectedConditions.urlMatches(".*/service/new.html"))
        }

        @Test
        fun `when edit a service with valid values then show service updated data`(webDriver: HtmlUnitDriver){
            val wait = WebDriverWait(webDriver, 1)
            `when save new service valid then show service data`(webDriver)

            webDriver.findElementById("edit_btn").click()

            wait.until(ExpectedConditions.urlMatches(".*/service/(\\d+)/edit\\.html"))


            fillForm(webDriver, name="edited service")

            webDriver.findElement(By.id("submit_btn")).click()

            wait.until(ExpectedConditions.urlMatches(".*/service/(\\d+)\\.html"))

            assertServiceShow(webDriver, name="edited service")
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
            webDriver.findElement<WebElement>(By.id("frm_default_field_string")).let{it.clear(); it.sendKeys(string)}
            webDriver.findElement<WebElement>(By.id("frm_default_field_number")).let{it.clear(); it.sendKeys(number.toString())}
            webDriver.findElement<WebElement>(By.id("frm_default_field_date"))
                    .let{it.clear(); it.sendKeys(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))}
        }

        @Test
        fun `when submit valid fake data then redirect to fakedata page`(webDriver:HtmlUnitDriver) {
            get(webDriver, "fake/new.html")

            fillForm(webDriver)


            webDriver.findElement(By.id("frm_default_submit")).submit()

            val wait = WebDriverWait(webDriver, 2)
            wait.until(ExpectedConditions.urlMatches("^http\\:\\/\\/localhost\\:\\d+\\/fake\\/\\d+\\.html\$"))

            val id = webDriver.findElement(By.id("fieldId")).text.toLong()
            val fake = FakeApi.FakeData.withId(id)
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
            assertThat(item.text).isEqualTo("must be greater than or equal to 0")
            assertThat(item.isDisplayed).isTrue()
        }
    }

    @Test
    fun `verify regex for fake url`(){
        assertThat("http://localhost:4455/fake/1111.html").containsPattern(STR_REGEX_FAKE_URL)
    }
}