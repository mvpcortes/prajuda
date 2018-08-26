package br.uff.mvpcortes.prajuda.workdir

import java.io.File

/**
 * Provides the work directory (that contains harvester files, etc)
 */
interface WorkDirectoryProvider {
    fun workDirectory(): File

}