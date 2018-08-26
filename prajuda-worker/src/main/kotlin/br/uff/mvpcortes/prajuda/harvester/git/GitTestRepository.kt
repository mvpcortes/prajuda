package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.util.FileUtils
import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import org.eclipse.jgit.api.Git

import java.io.File
import java.nio.file.Files

/**
 * This class will create repositories to be used by tests
 */
class GitTestRepository(val dir:File = FileUtils.createTempDirectory("test_repository")):AutoCloseable{

    val logger = loggerFor(GitTestRepository::class)

    companion object {

    val STR_CODE_MD =
            """
            [Dicentem turres](http://nomine.com/prior): ille.

                esports_namespace_trojan += scrollProtocol;
                terminal;
            """.trimIndent()

    val STR_MAIN_MD =
            """
            # Micat pectore decipis aliquisque bracchia quoque mando

            ## Per tantum

            *Lorem* markdownum falsi, te plura Aeolidae volucrem dextrae herbis inmanem
            """.trimIndent()

    }

    fun createRepository(){
        Git.init().setDirectory(dir).call().use { gitRepo ->

            //### ZERO COMMIT ###//
            //make zero commit
            //this is a commit with zero files
            createFile("prajuda/.gitkeep", "")
            gitRepo.add().addFilepattern("prajuda/.gitkeep").call()//this file should be ignored by harvester
            commitTag(gitRepo, "0")

            //### FIRST COMMIT ###//
            //make first commit
            createFile("prajuda/main.md", STR_MAIN_MD)
            //file out of prajuda directory
            createFile("root.md", "empty")

            //val pomFile = File(dir, "pom.xml")
            gitRepo.add().addFilepattern("prajuda/main.md").call()
            gitRepo.add().addFilepattern("root.md").call()
            commitTag(gitRepo, "1")

            //### SECOND COMMIT ###//
            //make directory and file
            createFile("prajuda/src/code.md", STR_CODE_MD)
            createFile("prajuda/main.md", "xuxu xaxa")
            gitRepo.add().addFilepattern("prajuda").call()
            commitTag(gitRepo, "2")

            //### THIRD COMMIT ###//
            //create a file and move other
            createFile("prajuda/src/user.md", "class user test content")
            createFile("prajuda/src/pc.md", "class pc test content")
            moveFile("prajuda/main.md", "prajuda/org/main.md")
            gitRepo.rm().addFilepattern("prajuda/main.md").call()
            gitRepo.add().addFilepattern("prajuda").call()
            commitTag(gitRepo, "3")

            //### FOURTH COMMIT ###//
            //remove a file
            createFile("/other_path/other_file.md", "other_content")
            createFile("/other_path/other_file.txt", "other_content.txt")
            deleteFile("prajuda/src/pc.md")
            gitRepo.rm().addFilepattern("prajuda/src/pc.md").call()
            gitRepo.add().addFilepattern("other_path").call()
            commitTag(gitRepo, "4")
        }
    }

    private fun commitTag(gitRepo: Git, tagName:String) {
        gitRepo.commit().setMessage("by mvpcortes: $tagName").call()
        gitRepo.tag().setName(tagName).call()
    }

    private fun deleteFile(f: String) {
        File(dir, f).delete()
    }

    private fun moveFile(fSource: String, fDest: String) {
        File(dir, fDest).parentFile.takeIf { !it.exists() }?.takeIf { it.mkdirs() }
        Files.move(File(dir, fSource).toPath(), File(dir, fDest).toPath())
    }

    private fun createFile(s: String, text: String) {
        val f = File(dir, s).absoluteFile
        f.parentFile.takeIf { !it.exists() }?.takeIf { it.mkdirs() }
        f.takeIf{!it.exists()}?.takeIf{it.createNewFile()}
        f.writeText(text)
    }


    fun deleteTags(): GitTestRepository {
        val listTag = Git.open(dir).tagList().call()

        Git.open(dir).tagDelete().setTags(*listTag.map{it.name}.toTypedArray()).call()

        return this
    }

    fun changeMasterTo(tag:String): GitTestRepository {
        Git.open(dir).use{
            it.checkout().setName("refs/tags/$tag").call()
            it.branchCreate().setForce(true).setName("master").call()
        }
        return this
    }

    fun getUri():String = dir.toURI().toString()

    fun deletePrajudaDirAndCommitTag(tag:String){
        val file = File(dir, "prajuda")
        file.tryDeleteRecursively()
        Git.open(dir).use {
            it.rm().addFilepattern("prajuda").call()
            commitTag(it, tag)
        }
    }

    override fun close() {
        try{dir.tryDeleteRecursively()}catch(e:Exception){logger.error("fail to delete directory", e)}
    }
}