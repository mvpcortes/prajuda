package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.GitTestRepository
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.harvester.exception.InvalidRepositoryFormatException
import br.uff.mvpcortes.prajuda.harvester.exception.NonClonedRepositoryException
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.service.config.WorkDirectoryProviderTestImpl
import com.github.vanroy.springboot.autoconfigure.data.jest.ElasticsearchJestAutoConfiguration
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.TestConfiguration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a GitHarvesterProcessor ")
internal class GitHarvesterProcessorTest {

    /**
     * Exclude  jest dependency
     */
    @TestConfiguration
    @SpringBootApplication(exclude = [(ElasticsearchJestAutoConfiguration::class)])
    class MyConfiguration

    @Test
    fun `the context is initied `(){

    }

    val workDirectoryProvider= WorkDirectoryProviderTestImpl()

    val configService = ConfigService(workDirectoryProvider)

    val harvester = GitHarvesterProcessor(configService)

    @BeforeAll
    fun initAll(){
        workDirectoryProvider.onStartedEvent(mock())
    }

    @AfterAll
    fun dropAll(){
        workDirectoryProvider.onClosedEvent(mock())
    }


    @Nested
    inner class `references a git repository` {
        var gitTestRepository: GitTestRepository = GitTestRepository()

        val harvestedList = HarvestedConsumerList()

        var service = PrajService()

        @BeforeEach
        fun init() {
            gitTestRepository.close()
            harvestedList.clear()
            gitTestRepository = GitTestRepository()
            gitTestRepository.createRepository()
            service = PrajServiceFixture.withRepository(gitTestRepository.getUri())
        }

        @AfterEach
        fun drop() {
            configService.deleteWorkDirectoryForHarvester(GitHarvesterProcessor.STR_GIT_HARVESTER_ID)//delete workdir of harvester
            gitTestRepository.close()
        }

        private fun `in a tag X and remote repository commited tag Y`(x:Int, y:Int) {
            gitTestRepository.changeMasterTo(x.toString())
            harvester.harvestComplete(service, harvestedList.consumer())

            service = PrajServiceFixture.withRepositoryAndTag(gitTestRepository.getUri(), x.toString())                //change tag of service
            harvestedList.clear()

            gitTestRepository.changeMasterTo(y.toString())

            harvester.harvest(service, harvestedList.consumer())
        }

        private fun assertHarvestedDeleted(id: Int, path: String) {
            assertThat(harvestedList[id].op).isEqualTo(HarvestedOp.DELETED)
            assertThat(harvestedList[id].doc.id).isNull()
            assertThat(harvestedList[id].doc.content).isBlank()
            assertThat(harvestedList[id].doc.path).isEqualTo(path)
            assertThat(harvestedList[id].doc.tag).isEmpty()
            assertThat(harvestedList[id].doc.serviceId).isEqualTo(PrajServiceFixture.DEFAULT_ID)
            assertThat(harvestedList[id].doc.serviceName).isNull()//we do not need a name here
        }

        private fun assertHarvestedUpdated(id: Int, path: String, content: String, tag: String) {
            assertThat(harvestedList[id].op).isEqualTo(HarvestedOp.UPDATED)
            assertThat(harvestedList[id].doc.id).isNull()
            assertThat(harvestedList[id].doc.content).isEqualTo(content)
            assertThat(harvestedList[id].doc.path).isEqualTo(path)
            assertThat(harvestedList[id].doc.tag).isEqualTo(tag)
            assertThat(harvestedList[id].doc.serviceId).isEqualTo(PrajServiceFixture.DEFAULT_ID)
            assertThat(harvestedList[id].doc.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
        }

        @Nested
        inner class `run harvesterComplete` {

            @Test
            fun `on a repository without document directory then fail`(){
                gitTestRepository.deletePrajudaDirAndCommitTag("xuxu")

                val exception = assertThrows<InvalidRepositoryFormatException>()
                { harvester.harvestComplete(service, harvestedList.consumer()) }

                assertThat(exception.message).containsSubsequence(
                        "Repository",
                         "does not have directory 'prajuda'")
            }

            @Test
            fun `doesnt have tags then fail `() {
                gitTestRepository.deleteTags()

                val exception = assertThrows<InvalidRepositoryFormatException>()
                { harvester.harvestComplete(service, harvestedList.consumer()) }

                assertThat(exception.message).containsSubsequence(
                        "Repository",
                        "'file:",
                        "' does not have a valid tag in 'master' branch")
            }

            @Test
            fun `with one file then harvest only this file`() {
                gitTestRepository.changeMasterTo("1")

                harvester.harvestComplete(service, harvestedList.consumer())

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

            @Test
            fun `with tree files on second commit then harvest these files`() {
                gitTestRepository.changeMasterTo("2")

                harvester.harvestComplete(service, harvestedList.consumer())

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

            @Test
            fun `with rename_file_on_third commit then harvest files`() {
                gitTestRepository.changeMasterTo("3")

                harvester.harvestComplete(service, harvestedList.consumer())

                harvestedList.sort()

                assertThat(harvestedList).hasSize(4)

                val harvested = harvestedList.filter { it.doc.path == "org/main.md" }.map{it.doc}.single()

                assertThat(harvested.id).isEqualTo(null)
                assertThat(harvested.path).isEqualTo("org/main.md")//moved
                assertThat(harvested.tag).isEqualTo("3")
                assertThat(harvested.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(harvested.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
                assertThat(harvested.content).isEqualTo("xuxu xaxa")
            }

            @Test
            fun `on fourth commit then harvest files`() {
                gitTestRepository.changeMasterTo("4")

                harvester.harvestComplete(service, harvestedList.consumer())

                harvestedList.assertFourthCommit()
            }
        }

        @Nested
        inner class `run simple (diff) harvester` {

            @Test
            fun `on a repository without document directory then fail`(){
                harvester.harvestComplete(service, harvestedList.consumer())
                harvestedList.clear()

                gitTestRepository.deletePrajudaDirAndCommitTag("xuxu")

                val exception = assertThrows<InvalidRepositoryFormatException>()
                { harvester.harvest(service, harvestedList.consumer()) }

                assertThat(exception.message).containsSubsequence(
                        "Repository",
                        "does not have directory 'prajuda'")
            }


            @Test
            fun `without cached(cloned) directory will run a complete harvester`() {
                val exception = assertThrows<NonClonedRepositoryException> {
                    harvester.harvest(service, harvestedList.consumer())
                }

                assertThat(exception.service.name).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
                assertThat(exception.dir.absolutePath).containsSubsequence(PrajServiceFixture.DEFAULT_NAME)
            }

            @ParameterizedTest(name = "run #{index} with tag [{0}]")
            @ValueSource(ints = [1, 2, 3, 4])
            fun in_same_tag_then_non_operation(argument: Int) {
                gitTestRepository.changeMasterTo(argument.toString())
                harvester.harvestComplete(service, harvestedList.consumer())
                harvestedList.clear()

                //change argument of service
                service = PrajServiceFixture.withRepositoryAndTag(gitTestRepository.getUri(), argument.toString())

                harvester.harvest(service, harvestedList.consumer())

                assertThat(harvestedList).isEmpty()
            }

            @Test
            fun `in empty repository and remote repository commited tag 1 then get one updates`() {
                `in a tag X and remote repository commited tag Y`(0, 1)

                harvestedList.sort()

                assertThat(harvestedList).hasSize(1)

                assertHarvestedUpdated(0, "main.md", GitTestRepository.STR_MAIN_MD, "1")
            }

            @Test
            fun `in tag 1 and remote repository commited tag 2 then get two updates`() {
                `in a tag X and remote repository commited tag Y`(1, 2)

                harvestedList.sort()

                assertThat(harvestedList).hasSize(2)

                assertHarvestedUpdated(0, "main.md", "xuxu xaxa", "2")
                assertHarvestedUpdated(1, "src/code.md", GitTestRepository.STR_CODE_MD, "2")
            }

            @Test
            fun `in tag 2 and remote repository commited tag 3 then get tree updates and one delete`() {
                `in a tag X and remote repository commited tag Y`(2, 3)

                harvestedList.sort()

                assertThat(harvestedList).hasSize(4)

                assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "3")
                assertHarvestedUpdated(1, "src/pc.md", "class pc test content", "3")
                assertHarvestedUpdated(2, "src/user.md", "class user test content", "3")
                assertHarvestedDeleted(3, "main.md")
            }

            @Test
            fun `in tag 3 and remote repository commited tag 4 then get one delete`() {
                `in a tag X and remote repository commited tag Y`(3, 4)

                harvestedList.sort()

                assertThat(harvestedList).hasSize(1)

                assertHarvestedDeleted(0, "src/pc.md")
            }

            @Test
            fun `in tag 2 and remote repository commited tag 4 then get two updates and one delete`() {
                `in a tag X and remote repository commited tag Y`(2, 4)
                harvestedList.assertDiffSecondToFouthCommit()
            }

            @Test
            fun `in tag 1 and remote repository commited tag 4 then get tree updates and one delete`() {
                `in a tag X and remote repository commited tag Y`(1, 4)

                harvestedList.sort()

                assertThat(harvestedList).hasSize(4)

                assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "4")
                assertHarvestedUpdated(1, "src/code.md", GitTestRepository.STR_CODE_MD, "4")
                assertHarvestedUpdated(2, "src/user.md", "class user test content", "4")
                assertHarvestedDeleted(3, "main.md")
            }
        }

    }
}