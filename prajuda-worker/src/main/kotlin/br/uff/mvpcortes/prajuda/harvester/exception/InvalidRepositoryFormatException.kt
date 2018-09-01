package br.uff.mvpcortes.prajuda.harvester.exception

import br.uff.mvpcortes.prajuda.model.PrajService

class InvalidRepositoryFormatException (msg:String, e:Exception?=null, service:PrajService?=null):HarvesterException(msg ,e, service)