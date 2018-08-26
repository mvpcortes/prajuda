package br.uff.mvpcortes.prajuda.harvester.exception

import br.uff.mvpcortes.prajuda.model.PrajService
import java.io.File

class NonClonedRepositoryException(val service:PrajService, val dir: File)
    :Exception("No repository for ${service.name} in directory ${dir.absolutePath}")
