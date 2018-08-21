package util

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import java.io.File
import java.lang.reflect.Parameter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import org.junit.jupiter.api.Assertions.assertThrows
import kotlin.reflect.KClass


@DisplayName("When a TempDirectory")
internal class TempDirectoryExtensionTest: TempDirectoryExtension(mock()) {

    val rootDirectory: Path = Files.createTempDirectory("test_temp_directory_extension" + UUID.randomUUID().toString().replace("-", "") )

    @BeforeEach
    fun init(){
        //to delete if a test fail or we forgot delete it
        rootDirectory.toFile().deleteOnExit()
    }

    @AfterEach
    fun after(){
        if(rootDirectory.toFile().exists())
            this.recursiveDelete(rootDirectory)
    }

    @Nested
    inner class `create a directory `{

        val tempDirectoryExtension = TempDirectoryExtension(rootDirectory)

        val directoryCreated = tempDirectoryExtension.createFolder()

        @Test
        fun `then directory is child of root directory`(){
            assertThat(directoryCreated.parentFile).isEqualTo(rootDirectory.toFile())
        }

        @Test
        fun `then directory returned is a directory `() {
            assertThat(directoryCreated).isDirectory()
        }

        @Test
        fun `then directory returned exists`() {
            assertThat(directoryCreated).exists()
        }

        @Test
        fun `then directory returned is the same of stored directory`(){
            assertThat(directoryCreated).isEqualTo(tempDirectoryExtension.tempDirectory)
        }
    }




    @Test
    fun `has recursiveDelete called then delete all`(){
        assertThat(rootDirectory.toFile()).exists()
        assertThat(rootDirectory.toFile()).isDirectory()

        this.recursiveDelete(rootDirectory)

        assertThat(rootDirectory.toFile()).doesNotExist()
    }

    @Nested
    inner class `will be deleted with a file `{
        val fileChild = File(rootDirectory.toFile(), "file_child")

        @BeforeEach
        fun init(){
            fileChild.createNewFile()
            fileChild.writeText("xuxu beleza")
        }

        @Test
        fun `then the child file has deleted too`(){
            assertThat(fileChild).exists()
            assertThat(fileChild).isFile()

            assertThat(fileChild.parentFile).isEqualTo(rootDirectory.toFile())
            recursiveDelete(rootDirectory)

            assertThat(fileChild).doesNotExist()
        }

        @Nested
        inner class `and a directory with files`{
            val dirChild = File(rootDirectory.toFile(), "dir")
            val fileCC   = File(dirChild, "ccFile")

            @BeforeEach
            fun init(){
                dirChild.mkdir()
                fileCC.createNewFile()
                fileCC.writeText("xuxu beleza 2")
            }

            @Test
            fun `then the child directory and files should be deleted`(){
                assertThat(dirChild).exists()
                assertThat(dirChild).isDirectory()
                assertThat(fileCC).exists()
                assertThat(fileCC).isFile()

                assertThat(fileCC.parentFile).isEqualTo(dirChild)
                assertThat(dirChild.parentFile).isEqualTo(rootDirectory.toFile())

                recursiveDelete(rootDirectory)

                assertThat(fileChild).doesNotExist()
                assertThat(fileCC).doesNotExist()
            }
        }
    }

    @Nested
    inner class `in the test cicle `{

        val tempDirectoryExtension = TempDirectoryExtension(rootDirectory)

        val extensionContext = mock<ExtensionContext>()
        val parameterContext = mock<ParameterContext>()

        @BeforeEach
        fun init(){
            tempDirectoryExtension.beforeEach(extensionContext)
        }

//        fun <T:Any> createParameter( clazz:Class<T>)= mock<Parameter>(){
//            on { getType() } doReturn clazz
//        }

        fun function_for_get_parameter(file:File, map:Map<String, String>){
            //fo nothing
        }

        fun <T:Any> getParameter(klass: KClass<T>):Parameter = this::class.java.declaredMethods
                .asSequence()
                .filter { it.name == "function_for_get_parameter" }
                .flatMap { it.parameters.asSequence() }
                .filter{it.type == klass.java}
                .single()

        @Nested
        inner class `does not have a parameter with annotation @TempDirectory `{
            val parameterContext = mock<ParameterContext> {
                on { isAnnotated(TempDirectory::class.java) } doReturn false
                on { parameter } doReturn getParameter(Map::class)
            }

            @Test
            fun `then return nothing for argument`(){

                val obj = tempDirectoryExtension.resolveParameter(parameterContext, extensionContext)
                assertThat(obj).isNull()
            }

        }


        @Nested
        inner class `has a parameter with annotation @TempDirectory `{

            @Nested
            inner class `and type non File `{
                val parameterContext = mock<ParameterContext> {
                    on { isAnnotated(TempDirectory::class.java) } doReturn true
                    on { parameter } doReturn getParameter(Map::class)
                }

                @AfterEach
                fun drop() {
                    reset(parameterContext)
                }

                @Test
                fun `then will fail because type of argument is not file`(){
                    val exception  = assertThrows(AssertionError::class.java
                    ) { tempDirectoryExtension.resolveParameter(parameterContext, extensionContext)}

                    assertThat(exception).hasMessage("The TempDirectory should be a File")
                }

            }

            @Nested
            inner class `and type File `{
                val parameterContext = mock<ParameterContext> {
                    on { isAnnotated(TempDirectory::class.java) } doReturn true
                    on { parameter }.doReturn( getParameter(File::class))
                }

                @AfterEach
                fun drop() {
                    reset(parameterContext)
                }

                @Nested
                inner class `then will resolver parameter `{
                    val anyValue = tempDirectoryExtension.resolveParameter(parameterContext, extensionContext)

                    @Test
                    fun `with new directory `() {


                        assertThat(anyValue).isInstanceOf(File::class.java)
                        val file = anyValue as File

                        assertThat(file).isDirectory()
                    }

                    @Test
                    fun `with file registed in the extension `() {
                        val file = anyValue as File
                        assertThat(file).isEqualTo(tempDirectoryExtension.tempDirectory)
                    }
                }
            }
        }

        @AfterEach
        fun drop(){
            tempDirectoryExtension.afterEach(extensionContext)
            tempDirectoryExtension.afterAll(extensionContext)
        }
    }
}