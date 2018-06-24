package br.uff.mvpcortes.prajuda

import br.uff.mvpcortes.prajuda.util.FileUtils
import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import com.google.common.io.Files
import org.eclipse.jgit.api.Git

import java.io.File

/**
 * This class will create repositories to be used by tests
 */
class GitTestRepository(val dir:File = FileUtils.createTempDirectory("test_repository")):AutoCloseable{

    val logger = loggerFor(GitTestRepository::class)

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




    fun createRepository(){
        Git.init().setDirectory(dir).call().use { gitRepo ->

            //make first commit
            createFile("prajuda/main.md", STR_MAIN_MD)
            //val pomFile = File(dir, "pom.xml")
            gitRepo.add().addFilepattern("prajuda/main.md").call()

            commitTag(gitRepo, "1")
            //make directory and file

            createFile("prajuda/src/code.md", STR_CODE_MD)
            createFile("prajuda/main.md", "xuxu xaxa")
            gitRepo.add().addFilepattern("prajuda/**").call()
            commitTag(gitRepo, "2")

            //create a file and move other
            createFile("prajuda/src/user.md", "class user test content")
            createFile("prajuda/src/pc.md", "class pc test content")
            moveFile("prajuda/main.md", "prajuda/org/main.md")
            gitRepo.rm().addFilepattern("prajuda/main.md").call()
            gitRepo.add().addFilepattern("prajuda/**").call()
            commitTag(gitRepo, "3")

            //remove a file
            deleteFile("prajuda/src/pc.md")
            gitRepo.rm().addFilepattern("prajuda/src/pc.md").call()
            commitTag(gitRepo, "4")
        }
    }

    private fun commitTag(gitRepo: Git, tagName:String) {
        gitRepo.commit().setMessage("by mvpcortes: ${tagName}").call()
        gitRepo.tag().setName(tagName).call()
    }

    private fun deleteFile(f: String) {
        File(dir, f).delete()
    }

    private fun moveFile(fSource: String, fDest: String):Unit {
        File(dir, fDest).parentFile.takeIf { !it.exists() }?.takeIf { it.mkdirs() }
        Files.move(File(dir, fSource), File(dir, fDest))
    }

    private fun createFile(s: String, text: String):Unit {
        val f = File(dir, s).absoluteFile
        f.parentFile.takeIf { !it.exists() }?.takeIf { it.mkdirs() }
        f.takeIf{!it.exists()}?.takeIf{it.createNewFile()}
        f.writeText(text)
    }


    fun changeMasterTo(tag:String):GitTestRepository{
        Git.open(dir).use{
            it.checkout().setName("refs/tags/"+tag).call()
            it.branchCreate().setForce(true).setName("master").call()
        }
        return this
    }

    fun getUri():String = dir.toURI().toString()

    override fun close() {
        try{dir.tryDeleteRecursively()}catch(e:Exception){logger.error("fail to delete directory", e)}
    }
}