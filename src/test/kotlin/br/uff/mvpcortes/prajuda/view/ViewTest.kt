package br.uff.mvpcortes.prajuda.view

import io.github.bonigarcia.DriverCapabilities
import io.github.bonigarcia.Options
import io.github.bonigarcia.SeleniumExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.Platform
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver.DOWNLOAD_IMAGES_CAPABILITY
import org.openqa.selenium.htmlunit.HtmlUnitDriver.JAVASCRIPT_ENABLED
import org.openqa.selenium.remote.CapabilityType.SUPPORTS_JAVASCRIPT
import org.openqa.selenium.remote.DesiredCapabilities
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(value=[SpringExtension::class, SeleniumExtension::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ViewTest{

    @LocalServerPort
    var port:Int = 0

    @DriverCapabilities
    var htmlUnitOptions = DesiredCapabilities(mapOf(
            "browserName" to "htmlunit",
            "version" to "",
            "platform" to Platform.ANY,
            SUPPORTS_JAVASCRIPT to true,
            JAVASCRIPT_ENABLED to true,
            DOWNLOAD_IMAGES_CAPABILITY to true
    ))

    fun get(webDriver:HtmlUnitDriver, path:String):HtmlUnitDriver{
        webDriver.manage().window().size = Dimension(360, 640)
        webDriver.get("http://localhost:$port/$path")
        return webDriver
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