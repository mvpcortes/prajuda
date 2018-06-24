package br.uff.mvpcortes.prajuda.service.config

import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderImpl.Companion.STR_AJUDA_DIR
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderImpl.Companion.STR_DOT_AJUDA_DIR
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import util.TempDirectory
import util.TempDirectoryExtension
import java.io.File


@ExtendWith(TempDirectoryExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a DirectoryProviderImpl ")
class WorkDirectoryProviderImplTest{

    @Test
    fun ` and the root directory exists then return it`(@TempDirectory rootDir:File){
        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir)

        val rootDir = workDirectoryProviderImpl.getRootDir()

        assertThat(rootDir).isNotNull()
    }

    @Test
    fun ` and the root directory is not a directory then return null`(@TempDirectory rootDir:File){
        val file = File(rootDir, "xuxu")
        file.createNewFile()
        assertThat(file).isFile()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=file)

        val rootDir = workDirectoryProviderImpl.getRootDir()

        assertThat(rootDir).isNull()
    }

    @Test
    fun ` and the root directory not exists then return null`(@TempDirectory rootDir:File){
        val file = File(rootDir, "xuxu")
        assertThat(file).doesNotExist()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=file)
        val rootDir = workDirectoryProviderImpl.getRootDir()

        assertThat(rootDir).isNull()
    }

    @Test
    fun ` and the root directory has the prajuda dir then return prajuda dir`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, STR_AJUDA_DIR)
        prajudaDir.mkdir()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir)

        val file = workDirectoryProviderImpl.getWorkDirInRootDirectory()

        assertThat(file).isNotNull()
        assertThat(file).isEqualTo(prajudaDir)
    }

    @Test
    fun ` and the root directory does not exists then return null`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, STR_AJUDA_DIR)
        assertThat(prajudaDir).doesNotExist()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=prajudaDir)

        val file = workDirectoryProviderImpl.getWorkDirInRootDirectory()

        assertThat(file).isNull()
    }

    @Test
    fun ` and the root directory does not have the prajuda dir then return new prajuda dir`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, STR_AJUDA_DIR)
        assertThat(prajudaDir).doesNotExist()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir)

        val file = workDirectoryProviderImpl.getWorkDirInRootDirectory()

        assertThat(file).isNull()
    }



    @Test
    fun ` and the root directory exists but prajuda is not a directory then return null`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, STR_AJUDA_DIR)
        prajudaDir.createNewFile()
        assertThat(prajudaDir).isFile()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir)

        val file = workDirectoryProviderImpl.getWorkDirInRootDirectory()

        assertThat(file).isNull()
    }

    @Test
    fun ` and the properties define a there is directory then return properties' directory`(@TempDirectory rootDir:File) {

        val propDir = File(rootDir, "prop_dir")
        propDir.mkdir()
        assertThat(propDir).isDirectory()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, strWorkDirProperties = propDir.absolutePath)

        val file = workDirectoryProviderImpl.getWorkDirInProperties()

        assertThat(file).isNotNull()
        assertThat(file).isDirectory()
        assertThat(file).isEqualTo(propDir)
    }

    @Test
    fun ` and the properties define a there is not path then create directory`(@TempDirectory rootDir:File) {

        val propDir = File(rootDir, "prop_dir")
        assertThat(propDir).doesNotExist()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, strWorkDirProperties = propDir.absolutePath)

        val file = workDirectoryProviderImpl.getWorkDirInProperties()


        assertThat(file).isNotNull()
        assertThat(file).isDirectory()
        assertThat(file).isEqualTo(propDir)
    }

    @Test
    fun ` and the properties define a there is file then return null`(@TempDirectory rootDir:File) {

        val propFile = File(rootDir, "prop_dir")
        propFile.createNewFile()
        assertThat(propFile).exists()
        assertThat(propFile).isFile()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, strWorkDirProperties = propFile.absolutePath)

        val file = workDirectoryProviderImpl.getWorkDirInProperties()


        assertThat(file).isNull()
    }

    @Test
    fun ` and the home_dir exists then create prajuda dir`(@TempDirectory rootDir:File) {

        val homeDir = createHomeDir(rootDir)

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, homeDir = homeDir)

        val file = workDirectoryProviderImpl.getWorkDirInHomeDir()


        assertThat(file).isNotNull()
        assertThat(file).isDirectory()
        assertThat(file!!.name).isEqualTo(STR_DOT_AJUDA_DIR)
        assertThat(file.parentFile).isEqualTo(homeDir)
    }

    @Test
    fun ` and the home_dir exists and prajuda already exists then get prajuda dir`(@TempDirectory rootDir:File) {

        val homeDir = createHomeDir(rootDir)
        val prajudaDir = createPrajudaDirInHome(homeDir)

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, homeDir = homeDir)

        val file:File? = workDirectoryProviderImpl.getWorkDirInHomeDir()


        assertThat(file).isNotNull()
        assertThat(file!!).isDirectory()
        assertThat(file!!).isEqualTo(prajudaDir)
        assertThat(file!!).isEqualTo(prajudaDir)
        assertThat(file!!.name).isEqualTo(STR_DOT_AJUDA_DIR)
        assertThat(file!!.parentFile).isEqualTo(homeDir)
    }

    @Test
    fun ` has a root directory valid then use it`(@TempDirectory rootDir:File){
        val prajudaDir = createPrajudaDir(rootDir)

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, homeDir = File(rootDir, "/xuxu"), strWorkDirProperties = null)

        val workDir:File? = workDirectoryProviderImpl.workDirectory()

        assertThat(workDir).isNotNull()

        assertThat(workDir).isEqualTo(prajudaDir)
        assertThat(workDir).isDirectory()
    }

    @Test
    fun ` does not have a root directory valid but has a valid_properties directory then use it`(@TempDirectory rootDir:File){

        val propDir = File(rootDir, "prop_dir")
        propDir.mkdir()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, homeDir = File(rootDir, "/xuxu"), strWorkDirProperties = propDir.absolutePath)

        val workDir:File? = workDirectoryProviderImpl.workDirectory()

        assertThat(workDir).isNotNull()

        assertThat(workDir).isEqualTo(propDir)
        assertThat(workDir).isDirectory()
    }

    @Test
    fun ` does not have a root directory valid but has a home dir directory then use it`(@TempDirectory rootDir:File){

        val homeDir = File(rootDir, "home_dir")
        homeDir.mkdir()

        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, homeDir = homeDir, strWorkDirProperties = null)

        val workDir:File? = workDirectoryProviderImpl.workDirectory()

        assertThat(workDir).isNotNull()

        assertThat(workDir?.parentFile).isEqualTo(homeDir)
        assertThat(workDir).isDirectory()
    }

    @Test
    fun ` does not have a valid directory then throw exception`(@TempDirectory rootDir:File){


        val workDirectoryProviderImpl = WorkDirectoryProviderImpl(rootDir=rootDir, homeDir = File(rootDir,"xuxu"), strWorkDirProperties = null)

        val exception = Assertions.assertThrows(IllegalStateException::class.java
        ) {  workDirectoryProviderImpl.workDirectory() }

        assertThat(exception).hasMessage("Cannot found a valid directory to workdir")
    }

    private fun createPrajudaDir(homeDir: File): File {
        val prajudaDir = File(homeDir, STR_AJUDA_DIR)
        prajudaDir.mkdir()
        assertThat(prajudaDir).exists()
        assertThat(prajudaDir).isDirectory()
        return prajudaDir
    }

    private fun createPrajudaDirInHome(homeDir: File): File {
        val prajudaDir = File(homeDir, STR_DOT_AJUDA_DIR)
        prajudaDir.mkdir()
        assertThat(prajudaDir).exists()
        assertThat(prajudaDir).isDirectory()
        return prajudaDir
    }

    private fun createHomeDir(rootDir: File): File {
        val homeDir = File(rootDir, "home_dir")
        homeDir.mkdir()
        assertThat(homeDir).exists()
        assertThat(homeDir).isDirectory()
        return homeDir
    }
}
