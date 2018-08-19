package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.controller.helper.TemplateHelper
import br.uff.mvpcortes.prajuda.service.RecommendationService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable

@Controller("/")
class HomeController (val recommendService:  RecommendationService){


    /**
     * @see https://github.com/thymeleaf/thymeleafsandbox-biglist-reactive
     */
    @GetMapping(value = ["home.html", "", "index.html"])
    fun home(model: Model): String {
        return TemplateHelper(model)
                .withAttr("services", ReactiveDataDriverContextVariable(recommendService.recommendServices()))
                .withPage("fragments/home").apply()
    }
}