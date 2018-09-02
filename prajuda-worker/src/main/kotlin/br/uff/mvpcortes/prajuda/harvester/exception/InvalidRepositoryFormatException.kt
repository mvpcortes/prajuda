package br.uff.mvpcortes.prajuda.harvester.exception

import br.uff.mvpcortes.prajuda.model.PrajService

class InvalidRepositoryFormatException (msg:String, service:PrajService, e:Exception?=null):HarvesterException(msg ,service, e)