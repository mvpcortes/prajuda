package br.uff.mvpcortes.prajuda.util

import java.io.File
import java.nio.file.Files
import java.util.*

//
fun File.tryDeleteRecursively() =  when {
    isFile -> delete()
    isDirectory -> deleteRecursively()
    else -> false
}

fun File.setDeleteOnExit():File{
    this.deleteOnExit()
    return this
}

object FileUtils{
    fun createTempDirectory(name:String):File = createTempDirectoryPath(name).toFile()

    fun createTempDirectoryPath(name:String) =Files.createTempDirectory(name + UUID.randomUUID().toString().replace("-", ""))
}
