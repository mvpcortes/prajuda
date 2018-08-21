package br.uff.mvpcortes.prajuda.controller

import br.uff.mvpcortes.prajuda.api.FakeApi
import br.uff.mvpcortes.prajuda.controller.helper.TemplateHelper
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("fake")
class FakeController{


    @GetMapping("new.html")
    fun getNew(model: Model):String{
        return TemplateHelper(model, "fake_new")
                .withAttrNotNull("fake", FakeApi.FakeData.empty())
                .apply()
    }

    @GetMapping("{id}.html")
    fun getFake(@PathVariable("id") id:Long, model: Model):String{
        return TemplateHelper(model, "fake")
                .withAttrNotNull("fake", FakeApi.FakeData.withId(id))
                .apply()
    }
}