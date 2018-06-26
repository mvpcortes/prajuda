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
    inner class `references a git repository`{
        var gitTestRepository: GitTestRepository=GitTestRepository()

        val harvestedList = HarvestedConsumerList()

        var service = PrajService()

        @BeforeEach
        fun init(){
            gitTestRepository.close()
            harvestedList.clear()
            gitTestRepository = GitTestRepository()
            gitTestRepository.createRepository()
            service = PrajServiceFixture.withRepository(gitTestRepository.getUri())
        }

        @AfterEach
        fun drop(){
            configService.deleteWorkDIrectoryForHarvester(GitHarvesterProcessor.STR_GIT_HARVESTER_ID)//delete workdir of harvester
            gitTestRepository.close()
        }

        @Nested
        inner class `run harvesterComplete` {
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
                assertThat(havested.sdoc.id).isEqualTo(null)
                assertThat(havested.sdoc.path).isEqualTo("main.md")
                assertThat(havested.sdoc.tag).isEqualTo("1")
                assertThat(havested.sdoc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(havested.sdoc.serviceName).isEqualTo("teste")
                assertThat(havested.sdoc.content).containsSubsequence(
                        "# Micat pectore decipis aliquisque bracchia quoque mando",
                        "## Per tantum",
                        "*Lorem* markdownum falsi, te plura Aeolidae volucrem dextrae herbis inmanem"
                )
            }

            @Test
            fun `with tree files on second commit then harvest these files`() {
                gitTestRepository.changeMasterTo("2")

                harvester.harvestComplete(service, harvestedList.consumer())

                assertThat(harvestedList).hasSize(2)

                assertThat(harvestedList[0].op).isEqualTo(HarvestedOp.UPDATED)
                assertThat(harvestedList[0].doc!!.id).isEqualTo(null)
                assertThat(harvestedList[0].doc!!.path).isEqualTo("main.md")
                assertThat(harvestedList[0].doc!!.tag).isEqualTo("2")
                assertThat(harvestedList[0].doc!!.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(harvestedList[0].doc!!.serviceName).isEqualTo("teste")
                assertThat(harvestedList[0].doc!!.content).isEqualTo("xuxu xaxa")

                assertThat(harvestedList[1].op).isEqualTo(HarvestedOp.UPDATED)
                assertThat(harvestedList[1].doc!!.id).isEqualTo(null)
                assertThat(harvestedList[1].doc!!.path).isEqualTo("src/code.md")
                assertThat(harvestedList[1].doc!!.tag).isEqualTo("2")
                assertThat(harvestedList[1].doc!!.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(harvestedList[1].doc!!.serviceName).isEqualTo("teste")
                assertThat(harvestedList[1].doc!!.content).isEqualTo(gitTestRepository.STR_CODE_MD)
            }

            @Test
            fun `with rename_file_on_third commit then harvest files`() {
                gitTestRepository.changeMasterTo("3")

                harvester.harvestComplete(service, harvestedList.consumer())

                assertThat(harvestedList).hasSize(4)

                val harvested = harvestedList.filter { it.doc!!.path == "org/main.md" }.map { it.doc!! }.single()

                assertThat(harvested.id).isEqualTo(null)
                assertThat(harvested.path).isEqualTo("org/main.md")//moved
                assertThat(harvested.tag).isEqualTo("3")
                assertThat(harvested.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(harvested.serviceName).isEqualTo("teste")
                assertThat(harvested.content).isEqualTo("xuxu xaxa")
            }

            @Test
            fun `on fourth commit then harvest files`() {
                gitTestRepository.changeMasterTo("4")

                harvester.harvestComplete(service, harvestedList.consumer())

                assertThat(harvestedList).hasSize(3)


                assertThat(harvestedList[0].op).isEqualTo(HarvestedOp.UPDATED)
                assertThat(harvestedList[0].sdoc.id).isEqualTo(null)
                assertThat(harvestedList[0].sdoc.path).isEqualTo("org/main.md")
                assertThat(harvestedList[0].sdoc.tag).isEqualTo("4")
                assertThat(harvestedList[0].sdoc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(harvestedList[0].sdoc.serviceName).isEqualTo("teste")
                assertThat(harvestedList[0].sdoc.content).isEqualTo("xuxu xaxa")

                assertThat(harvestedList[1].op).isEqualTo(HarvestedOp.UPDATED)
                assertThat(harvestedList[1].sdoc.id).isEqualTo(null)
                assertThat(harvestedList[1].sdoc.path).isEqualTo("src/user.md")
                assertThat(harvestedList[1].sdoc.tag).isEqualTo("4")
                assertThat(harvestedList[1].sdoc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(harvestedList[1].sdoc.serviceName).isEqualTo("teste")
                assertThat(harvestedList[1].sdoc.content).isEqualTo("class user test content")

                assertThat(harvestedList[2].op).isEqualTo(HarvestedOp.UPDATED)
                assertThat(harvestedList[2].sdoc.id).isEqualTo(null)
                assertThat(harvestedList[2].sdoc.path).isEqualTo("src/code.md")
                assertThat(harvestedList[2].sdoc.tag).isEqualTo("4")
                assertThat(harvestedList[2].sdoc.serviceId).isEqualTo("xxx.xxx.xxx.xxx")
                assertThat(harvestedList[2].sdoc.serviceName).isEqualTo("teste")
                assertThat(harvestedList[2].sdoc.content).isEqualTo(gitTestRepository.STR_CODE_MD)
            }
        }

        @Nested
        inner class `run simple harvester` {

            @Test
            fun `without cached(cloned) directory will run a complete harvester`(){
                val exception = assertThrows<NonClonedRepositoryException> {
                    harvester.harvest(service, harvestedList.consumer())
                }

                assertThat(exception.service.name).isEqualTo("teste")
                assertThat(exception.dir.absolutePath).containsSubsequence("teste")
            }

            @Test
            fun `in same tag then non operation`(){
                gitTestRepository.changeMasterTo("4")
                harvester.harvestComplete(service, harvestedList.consumer())
                harvestedList.clear()

                //change tag of service
                service = PrajServiceFixture.withRepositoryAndTag(gitTestRepository.getUri(), "4")

                harvester.harvest(service, harvestedList.consumer())

                assertThat(harvestedList).isEmpty()
            }

            @Test
            fun ` in tag 1 and remote repository commited tag 2 then get two updates`(){
                gitTestRepository.changeMasterTo("1")
                harvester.harvestComplete(service, harvestedList.consumer())
                service = PrajServiceFixture.withRepositoryAndTag(gitTestRepository.getUri(), "1")                //change tag of service
                harvestedList.clear()

                gitTestRepository.changeMasterTo("2")

                harvester.harvest(service, harvestedList.consumer())

                assertThat(harvestedList).hasSize(2)

                assertThat(harvestedList[0].op).isEqualTo(HarvestedOp.UPDATED)

                assertThat(harvestedList[1].op).isEqualTo(HarvestedOp.UPDATED)
            }
        }

    }
}