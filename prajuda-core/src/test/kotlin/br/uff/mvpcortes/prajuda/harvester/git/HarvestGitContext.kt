package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.GitTestRepository
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.harvester.exception.InvalidRepositoryFormatException
import br.uff.mvpcortes.prajuda.harvester.exception.NonClonedRepositoryException
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderTestImpl
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class HarvestGitContext{

    private val workDirectoryProvider= WorkDirectoryProviderTestImpl()

    private val configService = ConfigService(workDirectoryProvider)

    private val harvester = GitHarvesterProcessor(configService)

    private val gitTestRepository: GitTestRepository = GitTestRepository()

    private val harvestedList = HarvestedConsumerList()

    private var service = PrajServiceFixture.withRepository(gitTestRepository.getUri())

    val runComplete=RunComplete()

    val runDiff=RunDiff()

    fun getHarvesterConsumerComplete():(service:PrajService, (Harvested)->Unit)->Unit= harvester::harvestComplete

    fun getHarvesterConsumerDiff():(service:PrajService, (Harvested)->Unit)->Unit= harvester::harvest

    fun initClass(){
        workDirectoryProvider.onStartedEvent(mock())
    }

    fun dropClass(){
        workDirectoryProvider.onClosedEvent(mock())
    }

    fun initContext(){
        gitTestRepository.createRepository()
        harvestedList.clear()
    }


    fun dropContext(){
        gitTestRepository.close()
        configService.deleteWorkDirectoryForHarvester(GitHarvesterProcessor.STR_GIT_HARVESTER_ID)//delete workdir of harvester
    }

    fun setXAtRemotAndYAtLocal(x:Int, y:Int, harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
        gitTestRepository.changeMasterTo(x.toString())
        harvester.harvestComplete(service, harvestedList.consumer())

        service = PrajServiceFixture.withRepositoryAndTag(gitTestRepository.getUri(), x.toString())                //change tag of service
        harvestedList.clear()

        gitTestRepository.changeMasterTo(y.toString())

        harvesterFunc(service, harvestedList.consumer())
    }

    fun assertHarvestedDeleted(id: Int, path: String) {
        assertThat(harvestedList[id].op).isEqualTo(HarvestedOp.DELETED)
        assertThat(harvestedList[id].doc.id).isNull()
        assertThat(harvestedList[id].doc.content).isBlank()
        assertThat(harvestedList[id].doc.path).isEqualTo(path)
        assertThat(harvestedList[id].doc.tag).isEmpty()
        assertThat(harvestedList[id].doc.serviceId).isEqualTo(PrajServiceFixture.DEFAULT_ID)
        assertThat(harvestedList[id].doc.serviceName).isNull()//we do not need a name here
    }

    fun assertHarvestedUpdated(id: Int, path: String, content: String, tag: String) {
        assertThat(harvestedList[id].op).isEqualTo(HarvestedOp.UPDATED)
        assertThat(harvestedList[id].doc.id).isNull()
        assertThat(harvestedList[id].doc.content).isEqualTo(content)
        assertThat(harvestedList[id].doc.path).isEqualTo(path)
        assertThat(harvestedList[id].doc.tag).isEqualTo(tag)
        assertThat(harvestedList[id].doc.serviceId).isEqualTo(PrajServiceFixture.DEFAULT_ID)
        assertThat(harvestedList[id].doc.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
    }

    inner class RunComplete {

        fun `on a repository without document directory then fail`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit){
            gitTestRepository.deletePrajudaDirAndCommitTag("xuxu")

            val exception = assertThrows<InvalidRepositoryFormatException>()
            { harvesterFunc(service, harvestedList.consumer()) }

            assertThat(exception.message).containsSubsequence(
                    "Repository",
                    "does not have directory 'prajuda'")
        }

        fun `doesnt have tags then fail `(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            gitTestRepository.deleteTags()

            val exception = assertThrows<InvalidRepositoryFormatException>()
            { harvesterFunc(service, harvestedList.consumer()) }

            assertThat(exception.message).containsSubsequence(
                    "Repository",
                    "'file:",
                    "' does not have a valid tag in 'master' branch")
        }

        fun `with one file then harvest only this file`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            gitTestRepository.changeMasterTo("1")

            harvesterFunc(service, harvestedList.consumer())

            assertThat(harvestedList).hasSize(1)
            val havested = harvestedList[0]

            assertThat(havested.op).isEqualTo(HarvestedOp.UPDATED)
            assertThat(havested.doc.id).isEqualTo(null)
            assertThat(havested.doc.path).isEqualTo("main.md")
            assertThat(havested.doc.tag).isEqualTo("1")
            assertThat(havested.doc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(havested.doc.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
            assertThat(havested.doc.content).containsSubsequence(
                    "# Micat pectore decipis aliquisque bracchia quoque mando",
                    "## Per tantum",
                    "*Lorem* markdownum falsi, te plura Aeolidae volucrem dextrae herbis inmanem"
            )
        }

        fun `with tree files on second commit then harvest these files`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            gitTestRepository.changeMasterTo("2")

            harvesterFunc(service, harvestedList.consumer())

            harvestedList.sort()

            assertThat(harvestedList).hasSize(2)

            assertThat(harvestedList[0].op).isEqualTo(HarvestedOp.UPDATED)
            assertThat(harvestedList[0].doc.id).isEqualTo(null)
            assertThat(harvestedList[0].doc.path).isEqualTo("main.md")
            assertThat(harvestedList[0].doc.tag).isEqualTo("2")
            assertThat(harvestedList[0].doc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(harvestedList[0].doc.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
            assertThat(harvestedList[0].doc.content).isEqualTo("xuxu xaxa")

            assertThat(harvestedList[1].op).isEqualTo(HarvestedOp.UPDATED)
            assertThat(harvestedList[1].doc.id).isEqualTo(null)
            assertThat(harvestedList[1].doc.path).isEqualTo("src/code.md")
            assertThat(harvestedList[1].doc.tag).isEqualTo("2")
            assertThat(harvestedList[1].doc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(harvestedList[1].doc.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
            assertThat(harvestedList[1].doc.content).isEqualTo(GitTestRepository.STR_CODE_MD)
        }

        fun `with rename_file_on_third commit then harvest files`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            gitTestRepository.changeMasterTo("3")

            harvesterFunc(service, harvestedList.consumer())

            harvestedList.sort()

            assertThat(harvestedList).hasSize(4)

            val harvested = harvestedList.filter { it.doc.path == "org/main.md" }.map { it.doc }.single()

            assertThat(harvested.id).isEqualTo(null)
            assertThat(harvested.path).isEqualTo("org/main.md")//moved
            assertThat(harvested.tag).isEqualTo("3")
            assertThat(harvested.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
            assertThat(harvested.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
            assertThat(harvested.content).isEqualTo("xuxu xaxa")
        }

        fun `on fourth commit then harvest files`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            gitTestRepository.changeMasterTo("4")

            harvesterFunc(service, harvestedList.consumer())

            harvestedList.sort()

            assertThat(harvestedList).hasSize(3)


            assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "4")
            assertHarvestedUpdated(1, "src/code.md", GitTestRepository.STR_CODE_MD, "4")
            assertHarvestedUpdated(2, "src/user.md", "class user test content", "4")
        }
    }

    inner class RunDiff {


        fun `on a repository without document directory then fail`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit){
            harvester.harvestComplete(service, harvestedList.consumer())
            harvestedList.clear()

            gitTestRepository.deletePrajudaDirAndCommitTag("xuxu")

            val exception = assertThrows<InvalidRepositoryFormatException>()
            { harvesterFunc(service, harvestedList.consumer()) }

            assertThat(exception.message).containsSubsequence(
                    "Repository",
                    "does not have directory 'prajuda'")
        }



        fun `without cached(cloned) directory will run a complete harvester`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            val exception = assertThrows<NonClonedRepositoryException> {
                harvesterFunc(service, harvestedList.consumer())
            }

            assertThat(exception.service.name).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
            assertThat(exception.dir.absolutePath).containsSubsequence(PrajServiceFixture.DEFAULT_NAME)
        }

        @ParameterizedTest(name = "run #{index} with tag [{0}]")
        @ValueSource(ints = [1, 2, 3, 4])
        fun in_same_tag_then_non_operation(argument: Int, harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            gitTestRepository.changeMasterTo(argument.toString())
            harvester.harvestComplete(service, harvestedList.consumer())
            harvestedList.clear()

            //change argument of service
            service = PrajServiceFixture.withRepositoryAndTag(gitTestRepository.getUri(), argument.toString())

            harvesterFunc(service, harvestedList.consumer())

            assertThat(harvestedList).isEmpty()
        }


        fun `in empty repository and remote repository commited tag 1 then get one updates`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            setXAtRemotAndYAtLocal(0, 1, harvesterFunc)

            harvestedList.sort()

            assertThat(harvestedList).hasSize(1)

            assertHarvestedUpdated(0, "main.md", GitTestRepository.STR_MAIN_MD, "1")
        }


        fun `in tag 1 and remote repository commited tag 2 then get two updates`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            setXAtRemotAndYAtLocal(1, 2,harvesterFunc)

            harvestedList.sort()

            assertThat(harvestedList).hasSize(2)

            assertHarvestedUpdated(0, "main.md", "xuxu xaxa", "2")
            assertHarvestedUpdated(1, "src/code.md", GitTestRepository.STR_CODE_MD, "2")
        }


        fun `in tag 2 and remote repository commited tag 3 then get tree updates and one delete`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            setXAtRemotAndYAtLocal(2, 3,harvesterFunc)

            harvestedList.sort()

            assertThat(harvestedList).hasSize(4)

            assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "3")
            assertHarvestedUpdated(1, "src/pc.md", "class pc test content", "3")
            assertHarvestedUpdated(2, "src/user.md", "class user test content", "3")
            assertHarvestedDeleted(3, "main.md")
        }


        fun `in tag 3 and remote repository commited tag 4 then get one delete`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            setXAtRemotAndYAtLocal(3, 4, harvesterFunc)

            harvestedList.sort()

            assertThat(harvestedList).hasSize(1)

            assertHarvestedDeleted(0, "src/pc.md")
        }


        fun `in tag 2 and remote repository commited tag 4 then get two updates and one delete`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            setXAtRemotAndYAtLocal(2, 4, harvesterFunc)

            harvestedList.sort()

            assertThat(harvestedList).hasSize(3)


            assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "4")
            assertHarvestedUpdated(1, "src/user.md", "class user test content", "4")
            assertHarvestedDeleted(2, "main.md")
        }


        fun `in tag 1 and remote repository commited tag 4 then get tree updates and one delete`(harvesterFunc:(service:PrajService, (Harvested)->Unit)->Unit) {
            setXAtRemotAndYAtLocal(1, 4, harvesterFunc)

            harvestedList.sort()

            assertThat(harvestedList).hasSize(4)

            assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "4")
            assertHarvestedUpdated(1, "src/code.md", GitTestRepository.STR_CODE_MD, "4")
            assertHarvestedUpdated(2, "src/user.md", "class user test content", "4")
            assertHarvestedDeleted(3, "main.md")
        }
    }

}