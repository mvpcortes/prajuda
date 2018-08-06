package br.uff.mvpcortes.prajuda.controller.helper

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.Model
import org.springframework.validation.BindingResult

class TemplateHelper(val model:Model){

    var errorAttr:String?=null

    companion object {
        val mapper=ObjectMapper()
    }
    var template:String = "main_template"

    fun withPage(page:String):TemplateHelper{
        model.addAttribute("layout_page", page)
        return this
    }

    fun withAttrNotNull(name:String, obj:Any?):TemplateHelper{
        if(obj != null){
            model.addAttribute(name, obj)
        }
        return this
    }

    fun withAttr(name:String, obj:Any):TemplateHelper{
        model.addAttribute(name, obj)
        return this
    }

    fun apply():String{
        model.addAttribute("layout_title", "Praj título");
        return template
    }

}