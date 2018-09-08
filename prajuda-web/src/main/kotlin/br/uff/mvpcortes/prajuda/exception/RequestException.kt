package br.uff.mvpcortes.prajuda.exception

import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.web.server.ResponseStatusException
import sun.misc.Request


open class RequestException(message:String, val code:Int, e:Throwable?) : ResponseStatusException(HttpStatus.valueOf(code),
        "($code)$message", e){

    constructor(message:String, code:Int):this(message, code, null)
}


class PageNotFoundException(message: String, e:Throwable?=null)
    :ResponseStatusException(HttpStatus.NOT_FOUND, message, e)

class BadRequestException(message: String, e:Throwable?=null)
    :ResponseStatusException(HttpStatus.BAD_REQUEST, message, e)
