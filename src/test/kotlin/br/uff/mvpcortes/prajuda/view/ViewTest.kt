package br.uff.mvpcortes.prajuda.view

import br.uff.mvpcortes.prajuda.api.FakeApi
import br.uff.mvpcortes.prajuda.loggerFor
import io.github.bonigarcia.DriverCapabilities
import io.github.bonigarcia.SeleniumExtension
import org.apache.tools.ant.taskdefs.Javadoc
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.*
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver.DOWNLOAD_IMAGES_CAPABILITY
import org.openqa.selenium.htmlunit.HtmlUnitDriver.JAVASCRIPT_ENABLED
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.CapabilityType.SUPPORTS_JAVASCRIPT
import org.openqa.selenium.remote.DesiredCapabilities
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

    fun get(webDriver: WebDriver, path:String):WebDriver{

        //if is htmlUnit, force JS and download images
        if(webDriver is HtmlUnitDriver){
            webDriver.isJavascriptEnabled = true
            webDriver.isDownloadImages = true
        }


        //resize window to mobile form (mobile-first
        webDriver.manage().window().size = Dimension(360, 640)
        webDriver.get("http://localhost:$port/$path")
        return webDriver
    }

    @Nested
    inner class `The form ajax submit`{

        @Test
        fun `when get fakedata then render fields`(webDriver:HtmlUnitDriver) {
            webDriver.isJavascriptEnabled = true
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

            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
            val item = webDriver.findElement(By.xpath("//span[@data-error-for='fieldNumber' and @class='ajax-form-error-showed']"))
            assertThat(item.getAttribute("id")).isEqualTo("xptoNumber")
            assertThat(item.tagName).isEqualTo("span")
            assertThat(item.getAttribute("class")).isEqualTo("ajax-form-error-showed")
            assertThat(item.text).isEqualTo("deve ser maior ou igual a 0")
            assertThat(item.isDisplayed).isTrue()
        }
    }

    @Test
    fun test(){
        assertThat("http://localhost:4455/fake/1111.html").containsPattern("^http\\:\\/\\/localhost\\:\\d+\\/fake\\/\\d+\\.html\$")
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