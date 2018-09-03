package br.uff.mvpcortes.prajuda.exception

import org.springframework.http.HttpStatus

class PageNotFoundException(message: String):RequestException(message, HttpStatus.NOT_FOUND.value())
