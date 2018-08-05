package br.uff.mvpcortes.prajuda.controller.helper

import org.springframework.ui.Model

class TemplateHelper(val model:Model){

    var template:String = "main_template"

    fun withPage(page:String):TemplateHelper{
        model.addAttribute("layout_page", page)
        return this
    }

    fun apply():String{
        model.addAttribute("layout_title", "Praj título");
        return template
    }

}