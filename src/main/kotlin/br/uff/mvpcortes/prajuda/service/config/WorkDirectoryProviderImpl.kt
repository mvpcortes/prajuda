package br.uff.mvpcortes.prajuda.service.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class WorkDirectoryProviderImpl(
        @Value("\${prajuda.work-dir:#{null}}")
        private val strWorkDirProperties:String? = null,
        private val rootDir:File=File("/"),
        private val homeDir:File=File(System.getProperty("user.home"))
): WorkDirectoryProvider {

    companion object {
        val STR_AJUDA_DIR:String = "prajuda"
        val STR_DOT_AJUDA_DIR = ".${STR_AJUDA_DIR}"
    }

    private val logger:Logger = LoggerFactory.getLogger(WorkDirectoryProviderImpl::class.java)


    override fun workDirectory(): File {
        return getWorkDirInRootDirectory()
                ?:getWorkDirInProperties()
                ?:getWorkDirInHomeDir()
                ?:throw IllegalStateException("Cannot found a valid directory to workdir");
    }

    fun getWorkDirInHomeDir(): File? {
        try{
            return homeDir
                    .takeIf { existsDirectory(it) }
                    ?.let    {File(it, STR_DOT_AJUDA_DIR)}
                    ?.takeIf { existsDirectoryOrCreate(it) }
        }catch(e:Exception){
            return null;
        }
    }

    //
    fun getWorkDirInProperties(): File? {
        try {
            return strWorkDirProperties
                    ?.takeIf { !it.isBlank() }
                    ?.let { File(it) }
                    ?.takeIf { existsDirectoryOrCreate(it) }
        } catch (e: Exception) {
            return null
        }
    }

    //
    fun getWorkDirInRootDirectory(): File?  {
            return getRootDir()
                ?.let {  File(it, STR_AJUDA_DIR) }
                ?.takeIf{ existsDirectory(it) }
    }


    fun getRootDir():File?= this.rootDir.takeIf { existsDirectory(it) }

    protected fun existsDirectoryOrCreate(it: File) = existsDirectory(it) || it.mkdir()

    protected fun existsDirectory(it: File) = it.exists() && it.isDirectory()

}