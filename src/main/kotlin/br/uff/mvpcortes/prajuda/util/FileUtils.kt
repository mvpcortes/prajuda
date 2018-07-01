package br.uff.mvpcortes.prajuda.util

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

//
fun File.tryDeleteRecursively() =  when {
    isFile -> delete()
    isDirectory -> deleteRecursively()
    else -> false
}

object FileUtils{
    fun createTempDirectory(name:String):File = createTempDirectoryPath(name).toFile()

    fun createTempDirectoryPath(name:String): Path = Files.createTempDirectory(name + UUID.randomUUID().toString().replace("-", ""))!!

}
