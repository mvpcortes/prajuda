package br.uff.mvpcortes.prajuda.view

import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.springframework.boot.web.server.LocalServerPort
import java.util.concurrent.TimeUnit

abstract class AbstractViewTest {

    @LocalServerPort
    var port:Int = 0

    fun get(webDriver: WebDriver, path:String): WebDriver {
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

}