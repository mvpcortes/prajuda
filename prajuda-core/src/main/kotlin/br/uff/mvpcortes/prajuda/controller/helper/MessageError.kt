package br.uff.mvpcortes.prajuda.controller.helper
import org.springframework.validation.ObjectError

class MessageError(objectError: ObjectError, val objectName:String=objectError.objectName, val message:String=objectError.defaultMessage?:"no message")
