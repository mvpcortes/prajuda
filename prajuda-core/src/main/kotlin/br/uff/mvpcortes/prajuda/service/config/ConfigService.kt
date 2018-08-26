package br.uff.mvpcortes.prajuda.service.config

import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import org.springframework.stereotype.Service
import java.io.File

/**
 * PrajService to get commons values and operations.
 *
 */
@Service
class ConfigService(private val workDirectoryProvider: WorkDirectoryProvider){

    fun getWorkDirectoryForHarvester(idHarvester:String) =
            File(workDirectoryProvider.workDirectory(), idHarvester)
                    .takeIf { (it.exists() && it.isDirectory) || it.mkdir()}
                    ?:throw IllegalStateException("Cannot create workdir for $idHarvester")

    fun deleteWorkDirectoryForHarvester(idHarvester: String)=
        File(workDirectoryProvider.workDirectory(), idHarvester).tryDeleteRecursively()

}