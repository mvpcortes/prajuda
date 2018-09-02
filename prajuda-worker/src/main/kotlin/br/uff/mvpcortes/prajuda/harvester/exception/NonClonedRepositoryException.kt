package br.uff.mvpcortes.prajuda.harvester.exception

import br.uff.mvpcortes.prajuda.model.PrajService
import java.io.File

class NonClonedRepositoryException(service:PrajService, val dir: File)
    :HarvesterException("No repository for ${service.name} in directory ${dir.absolutePath}", service, null)
