package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.controller.helper.TemplateHelper
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller("/")
class HomeController {


    /**
     * @see https://github.com/thymeleaf/thymeleafsandbox-biglist-reactive
     */
    @GetMapping(value = ["home.html", "", "index.html"])
    fun home(model: Model): String {
        return TemplateHelper(model).withPage("fragments/home").apply()
    }
}