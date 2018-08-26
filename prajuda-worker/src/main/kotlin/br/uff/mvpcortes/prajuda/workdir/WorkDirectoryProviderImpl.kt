package br.uff.mvpcortes.prajuda.workdir

import br.uff.mvpcortes.prajuda.config.WorkerProperties
import br.uff.mvpcortes.prajuda.model.PrajService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class WorkDirectoryProviderImpl(
        private val workerProperties: WorkerProperties,
        private val rootDir:File=File("/"),
        private val homeDir:File=File(System.getProperty("user.home"))
): WorkDirectoryProvider {

    companion object {
        const val STR_AJUDA_DIR:String = PrajService.STR_AJUDA_DIR
        const val STR_DOT_AJUDA_DIR = ".$STR_AJUDA_DIR"
    }

    private val logger:Logger = LoggerFactory.getLogger(WorkDirectoryProviderImpl::class.java)


    override fun workDirectory(): File {
        return getWorkDirInRootDirectory()
                ?:getWorkDirInProperties()
                ?:getWorkDirInHomeDir()
                ?:throw IllegalStateException("Cannot found a valid directory to workdir")
    }

    fun getWorkDirInHomeDir(): File? {
           return homeDir
                    .takeIf { existsDirectory(it) }
                    ?.let    {File(it, STR_DOT_AJUDA_DIR)}
                    ?.takeIf { existsDirectoryOrCreate(it) }

    }

    //
    fun getWorkDirInProperties(): File? {
        return  workerProperties.workerDir
                    ?.takeIf { !it.isBlank() }
                    ?.let { File(it) }
                    ?.takeIf { existsDirectoryOrCreate(it) }
    }

    //
    fun getWorkDirInRootDirectory(): File?  {
            return getRootDir()
                ?.let {  File(it, STR_AJUDA_DIR) }
                ?.takeIf{ existsDirectory(it) }
    }


    fun getRootDir():File?= this.rootDir.takeIf { existsDirectory(it) }

    private fun existsDirectoryOrCreate(it: File) = existsDirectory(it) || it.mkdir()

    private fun existsDirectory(it: File) = it.exists() && it.isDirectory

}