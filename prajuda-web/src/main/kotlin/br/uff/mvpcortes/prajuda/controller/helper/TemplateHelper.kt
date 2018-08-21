package br.uff.mvpcortes.prajuda.controller.helper

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.Model

class TemplateHelper(private val model:Model, private val template:String="main_template"){

    var errorAttr:String?=null

    companion object {
        val mapper=ObjectMapper()
    }

    fun withPage(page:String): TemplateHelper {
        model.addAttribute("layout_page", page)
        return this
    }

    fun withAttrNotNull(name:String, obj:Any?): TemplateHelper {
        if(obj != null){
            model.addAttribute(name, obj)
        }
        return this
    }

    fun withAttr(name:String, obj:Any): TemplateHelper {
        model.addAttribute(name, obj)
        return this
    }

    fun apply():String{
        model.addAttribute("layout_title", "Praj título")
        return template
    }

}