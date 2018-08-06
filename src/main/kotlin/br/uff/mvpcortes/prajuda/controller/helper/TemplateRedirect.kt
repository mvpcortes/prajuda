package br.uff.mvpcortes.prajuda.controller.helper

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.util.LinkedMultiValueMap
import org.springframework.validation.BindingResult
import org.springframework.web.util.UriComponentsBuilder


class TemplateRedirect(val redirect:String="") {

    var mapParams =  LinkedMultiValueMap<String, String>()


    companion object {
        val mapper = ObjectMapper()
        const val STR_ERROR_ATTR="validation_error";
    }

    fun withError(bindingResult: BindingResult): TemplateRedirect {
        val errors = bindingResult.allErrors.map { MessageError(it) }.let{ TemplateHelper.mapper.writeValueAsString(it)}
        mapParams[STR_ERROR_ATTR] = mapper.writeValueAsString(errors)
        return this
    }

    fun apply()= UriComponentsBuilder.newInstance()
                .scheme("redirect")
                .path(redirect)
                .queryParams(mapParams)
                .build().toUriString()

    fun addParam(name: String, obj: Any): TemplateRedirect{
        mapParams[name] = mapper.writeValueAsString(obj)
        return this
    }
}