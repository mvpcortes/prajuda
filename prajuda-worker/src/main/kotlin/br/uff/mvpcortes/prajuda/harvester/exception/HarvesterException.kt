package br.uff.mvpcortes.prajuda.harvester.exception

import br.uff.mvpcortes.prajuda.model.PrajService

open class HarvesterException(message:String, val service: PrajService, throwable:Throwable?=null):RuntimeException(message, throwable)