package util

import org.junit.jupiter.api.extension.*
import java.io.IOException
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


/**
 * @see https://dev.to/vga/migration-from-junit-4-to-junit-5-19d6
 */
open class TempDirectoryExtension(
        val root:Path = Files.createTempDirectory("junit_temp_directory_" + randomStr() ))
        : BeforeEachCallback,
        AfterEachCallback,
        AfterAllCallback,
        ParameterResolver{

    companion object {
        fun randomStr()= UUID.randomUUID().toString().replace("-", "")
    }

    var tempDirectory:File? = null

    /**
     * Delete directory
     */
    override fun afterAll(p0: ExtensionContext?) {
        recursiveDelete(root)
    }

    override fun afterEach(extensionContext: ExtensionContext) {
        tempDirectory.let{this::recursiveDelete}
        tempDirectory=null
    }

    @Throws(IOException::class)
    override fun beforeEach(extensionContext: ExtensionContext) {
    }


    protected fun recursiveDelete(pathToBeDeleted: Path) {
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .forEach() { it.toFile().delete() }
    }

    override fun supportsParameter(parameterContext: ParameterContext?, p1: ExtensionContext?): Boolean {
        return (parameterContext?.parameter?.type == File::class.java)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? {
        this.tempDirectory=null
        return if(parameterContext.isAnnotated(TempDirectory::class.java)) {
            assert(File::class.java == parameterContext.parameter.type, { "The TempDirectory should be a File" } )
            createFolder()
        }else {  null }
    }

    fun createFolder(): File {
        tempDirectory = File(root.toFile(), randomStr())
        tempDirectory!!.mkdir()
        return tempDirectory!!
    }

}