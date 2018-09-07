package br.uff.mvpcortes.prajuda.exception

import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import sun.misc.Request


open class RequestException(message:String, val code:Int, e:Throwable?) :RuntimeException("($code)$message", e){

    constructor(message:String, code:Int):this(message, code, null)
}


class PageNotFoundException(message: String, e:Throwable?=null)
    :RequestException(message, HttpStatus.NOT_FOUND.value(), e)

class BadRequestException(message: String, e:Throwable?=null)
    :RequestException(message, HttpStatus.BAD_REQUEST.value(), e)
