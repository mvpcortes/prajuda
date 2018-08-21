package br.uff.mvpcortes.prajuda.service.config

import java.io.File

/**
 * Provides the work directory (that contains harvester files, etc)
 */
interface WorkDirectoryProvider {
    fun workDirectory(): File

}