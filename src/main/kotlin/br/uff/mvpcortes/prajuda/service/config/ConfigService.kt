package br.uff.mvpcortes.prajuda.service.config

import org.springframework.stereotype.Service
import java.io.File

/**
 * Service to get commons values and operations.
 *
 */
@Service
class ConfigService(val workDirectoryProvider: WorkDirectoryProvider){

    fun getWorkDirectoryForHarvester(idHarvester:String) =
            File(workDirectoryProvider.workDirectory(), idHarvester)
                    .takeIf { (it.exists() && it.isDirectory()) || it.mkdir()}
                    ?:throw IllegalStateException("Cannot create workdir for ${idHarvester}")

}