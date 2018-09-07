package br.uff.mvpcortes.prajuda.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FileUtilsTest{

    var tempDir = FileUtils.createTempDirectory("xuxu")

    @BeforeEach
    fun init(){
        tempDir.tryDeleteRecursively()
        tempDir = FileUtils.createTempDirectory("xuxu")
    }

    @AfterEach
    fun destroy(){
       tempDir.tryDeleteRecursively()
    }

    @Test
    fun `verify_directory_is_created`(){
        assertThat(tempDir).isDirectory()
    }

    @Test
    fun `verify we can create sub directory`(){
        val fileChild = File(tempDir, "xaxa.txt")
        fileChild.createNewFile()

        assertThat(fileChild).isFile()
    }
}