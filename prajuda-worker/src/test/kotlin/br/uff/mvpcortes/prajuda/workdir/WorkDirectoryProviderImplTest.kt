package br.uff.mvpcortes.prajuda.workdir

import br.uff.mvpcortes.prajuda.config.WorkerProperties
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.workdir.WorkDirectoryProviderImpl.Companion.STR_DOT_AJUDA_DIR
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import util.TempDirectory
import util.TempDirectoryExtension
import java.io.File


@ExtendWith(TempDirectoryExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a DirectoryProviderImpl ")
class WorkDirectoryProviderImplTest{

    val workerProperties:WorkerProperties = mock {    }

    @Test
    fun `and the root directory exists then return it`(@TempDirectory rootDir:File){
        val workDirectoryProviderImpl = createWorkDirectoryProvider(rootDir)

        val rootDir = workDirectoryProviderImpl.getRootDir()

        assertThat(rootDir).isNotNull()
    }

    @Test
    fun `and the root directory is not a directory then return null`(@TempDirectory rootDir:File){
        val file = File(rootDir, "xuxu")
        file.createNewFile()
        assertThat(file).isFile()

        val workDirectoryProviderImpl =  createWorkDirectoryProvider(file)

        val rootDir = workDirectoryProviderImpl.getRootDir()

        assertThat(rootDir).isNull()
    }

    @Test
    fun `and the root directory not exists then return null`(@TempDirectory rootDir:File){
        val file = File(rootDir, "xuxu")

        assertThat(file).doesNotExist()

        val workDirectoryProviderImpl = createWorkDirectoryProvider(file)

        val rootDir = workDirectoryProviderImpl.getRootDir()

        assertThat(rootDir).isNull()
    }

    @Test
    fun `and the root directory has the prajuda dir then return prajuda dir`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, PrajService.STR_AJUDA_DIR)
        prajudaDir.mkdir()

        createWorkDirectoryProvider(rootDir)
            .getWorkDirInRootDirectory()
                    .let{
                        assertThat(it).isNotNull()
                        assertThat(it).isEqualTo(prajudaDir)
                    }
    }

    @Test
    fun `and the root directory does not exists then return null`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, PrajService.STR_AJUDA_DIR)
        assertThat(prajudaDir).doesNotExist()

        createWorkDirectoryProvider(prajudaDir)
                .let{
                    val file = it.getWorkDirInRootDirectory()
                    assertThat(file).isNull()
                }
    }

    @Test
    fun `and the root directory does not have the prajuda dir then return new prajuda dir`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, PrajService.STR_AJUDA_DIR)
        assertThat(prajudaDir).doesNotExist()

        createWorkDirectoryProvider(rootDir)
                .let{
                    val file = it.getWorkDirInRootDirectory()
                    assertThat(file).isNull()
                }

    }



    @Test
    fun `and the root directory exists but prajuda is not a directory then return null`(@TempDirectory rootDir:File) {

        val prajudaDir = File(rootDir, PrajService.STR_AJUDA_DIR)
        prajudaDir.createNewFile()
        assertThat(prajudaDir).isFile()

        createWorkDirectoryProvider(rootDir).let{
            val file = it.getWorkDirInRootDirectory()
            assertThat(file).isNull()
        }
    }

    @Test
    fun `and the properties define a there is directory then return properties' directory`(@TempDirectory rootDir:File) {

        val propDir = setFileOnProperties {
            val f = File(rootDir, "prop_dir")
            f.mkdir()
            f

        }

        assertThat(propDir).isDirectory()

        createWorkDirectoryProvider(rootDir)
                .let {
                    val file = it.getWorkDirInProperties()
                    assertThat(file).isNotNull()
                    assertThat(file).isDirectory()
                    assertThat(file).isEqualTo(propDir)
                }
    }

    @Test
    fun `and the properties define a there is not path then create directory`(@TempDirectory rootDir:File) {

        val propDir = setFileOnProperties {  File(rootDir, "prop_dir") }

        assertThat(propDir).doesNotExist()

        createWorkDirectoryProvider(rootDir)
                .let{
                    val file = it.getWorkDirInProperties()
                    assertThat(file).isNotNull()
                    assertThat(file).isDirectory()
                    assertThat(file).isEqualTo(propDir)
                }

    }

    @Test
    fun `and the properties define a there is file then return null`(@TempDirectory rootDir:File) {


        val propFile = setFileOnProperties { File(rootDir, "prop_dir").let{it.createNewFile(); it} }

        assertThat(propFile).exists()
        assertThat(propFile).isFile()

        createWorkDirectoryProvider(rootDir)
                .let{
                    val file = it.getWorkDirInProperties()
                    assertThat(file).isNull()
                }
    }

    @Test
    fun `and the home_dir exists then create prajuda dir`(@TempDirectory rootDir:File) {

        val homeDir = createHomeDir(rootDir)

        createWorkDirectoryProvider(rootDir=rootDir, homeDir=homeDir)
                .let{
                    val file = it.getWorkDirInHomeDir()
                    assertThat(file).isNotNull()
                    assertThat(file).isDirectory()
                    assertThat(file!!.name).isEqualTo(WorkDirectoryProviderImpl.STR_DOT_AJUDA_DIR)
                    assertThat(file.parentFile).isEqualTo(homeDir)
                }
    }

    @Test
    fun `and the home_dir exists and prajuda already exists then get prajuda dir`(@TempDirectory rootDir:File) {

        val homeDir = createHomeDir(rootDir)
        val prajudaDir = createPrajudaDirInHome(homeDir)
        createWorkDirectoryProvider(rootDir=rootDir, homeDir = homeDir)
                .let {
                    val file = it.getWorkDirInHomeDir()
                    assertThat(file).isNotNull()
                    assertThat(file!!).isDirectory()
                    assertThat(file).isEqualTo(prajudaDir)
                    assertThat(file).isEqualTo(prajudaDir)
                    assertThat(file.name).isEqualTo(WorkDirectoryProviderImpl.STR_DOT_AJUDA_DIR)
                    assertThat(file.parentFile).isEqualTo(homeDir)
                }

    }

    @Test
    fun `has a root directory valid then use it`(@TempDirectory rootDir:File){
        val prajudaDir = createPrajudaDir(rootDir)

        setFileOnProperties { null }

        createWorkDirectoryProvider(rootDir=rootDir, homeDir = File(rootDir, "/xuxu"))
                .let{
                    val workDir:File? = it.workDirectory()

                    assertThat(workDir).isNotNull()

                    assertThat(workDir).isEqualTo(prajudaDir)
                    assertThat(workDir).isDirectory()
                }

    }

    @Test
    fun `does not have a root directory valid but has a valid_properties directory then use it`(@TempDirectory rootDir:File){

        val propDir = setFileOnProperties{ File(rootDir, "prop_dir").let{ it.mkdir(); it}}

        createWorkDirectoryProvider(rootDir=rootDir, homeDir = File(rootDir, "/xuxu"))
                .let{
                    val workDir:File? = it.workDirectory()
                    assertThat(workDir).isNotNull()
                    assertThat(workDir).isEqualTo(propDir)
                    assertThat(workDir).isDirectory()
                }
    }

    @Test
    fun `does not have a root directory valid but has a home dir directory then use it`(@TempDirectory rootDir:File){

        val homeDir = File(rootDir, "home_dir")
        homeDir.mkdir()

        setFileOnProperties { null }

        createWorkDirectoryProvider(rootDir=rootDir, homeDir = homeDir)
                .let {
                    val workDir: File? = it.workDirectory()

                    assertThat(workDir).isNotNull()

                    assertThat(workDir?.parentFile).isEqualTo(homeDir)
                    assertThat(workDir).isDirectory()
                }
    }

    @Test
    fun `does not have a valid directory then throw exception`(@TempDirectory rootDir:File){

        setFileOnProperties { null }

        val workDirectoryProviderImpl = createWorkDirectoryProvider(rootDir=rootDir, homeDir = File(rootDir,"xuxu"))

        val exception = Assertions.assertThrows(IllegalStateException::class.java
        ) {  workDirectoryProviderImpl.workDirectory() }

        assertThat(exception).hasMessage("Cannot found a valid directory to workdir")
    }

    private fun createPrajudaDir(homeDir: File): File {
        val prajudaDir = File(homeDir, PrajService.STR_AJUDA_DIR)
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

    private fun setFileOnProperties(block:()->File?):File?{
        val file = block()
        doReturn(file?.absolutePath).whenever(workerProperties).workerDir
        return file
    }

    private fun createWorkDirectoryProvider(rootDir:File, homeDir:File=File(System.getProperty("user.home"))):WorkDirectoryProviderImpl{
        return WorkDirectoryProviderImpl(rootDir = rootDir,
                workerProperties = workerProperties,
                homeDir = homeDir)
    }
}
