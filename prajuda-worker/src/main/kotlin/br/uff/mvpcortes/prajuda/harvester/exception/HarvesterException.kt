package br.uff.mvpcortes.prajuda.harvester.exception

import br.uff.mvpcortes.prajuda.model.PrajService

open class HarvesterException(message:String, throwable:Throwable?=null, val service: PrajService?=null):RuntimeException(message, throwable)