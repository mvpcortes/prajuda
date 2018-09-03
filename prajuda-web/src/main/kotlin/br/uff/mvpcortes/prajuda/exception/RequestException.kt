package br.uff.mvpcortes.prajuda.exception

open class RequestException(message:String, val code:Int, e:Exception?) :RuntimeException("($code)$message", e){

    constructor(message:String, code:Int):this(message, code, null)
}