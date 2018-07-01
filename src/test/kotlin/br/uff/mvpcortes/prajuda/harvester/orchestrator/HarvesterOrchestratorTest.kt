package br.uff.mvpcortes.prajuda.harvester.orchestrator

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.FluxHarvesterProcessor
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.harvester.git.HarvestedConsumerList
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.HarvestedFixture
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import reactor.core.publisher.Flux
import java.util.*
import java.util.function.Consumer

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("When a HarvesterOrchestrator ")
class HarvesterOrchestratorTest{

    val harvesterProcessor: FluxHarvesterProcessor = mock()

    val harvesterConsumerList=HarvestedConsumerList()
    val harvestedConsumer: Consumer<Harvested> = harvesterConsumerList.javaConsumer()

    val prajServiceDAO: PrajServiceDAO = mock()

    val harvesterOrchestrator= HarvesterOrchestrator(harvesterProcessor, harvestedConsumer, prajServiceDAO)

    val mapPrajServiceUsed:MutableMap<String, PrajService>  =  HashMap()

    @BeforeEach
    fun init(){

    }

    fun addPrajService(name:String){
        val prajService = PrajServiceFixture.withName(name)
        mapPrajServiceUsed.put(name, prajService)
        doAnswer{
            (it.arguments[0] as String?)
                    ?.let{itName-> mapPrajServiceUsed.get(itName)}
                    ?.let{itPA->Optional.of(itPA)}
        }.whenever(prajServiceDAO).findById(any())

    }
    @AfterEach
    fun drop(){
        Mockito.reset(harvesterProcessor, prajServiceDAO)
        harvesterConsumerList.clear()
        mapPrajServiceUsed.clear()
    }

    private fun createFlux(it: InvocationOnMock): Flux<Harvested> {
        return createFlux(it, HarvestedOp.UPDATED)
    }

    private fun createFlux(it: InvocationOnMock, vararg operations:HarvestedOp): Flux<Harvested> {
        val arg = it.arguments[0] as PrajService
        val operations2 = operations.mapIndexed(){i, element-> HarvestedFixture.create(element, arg.name, i)}
        return Flux.just(*operations2.toTypedArray())
    }


    @Nested
    inner class `has one harvesterProcessor`{

        val serviceId = "test"

        @BeforeEach
        fun before(){
            doReturn(listOf(serviceId)).`when`(prajServiceDAO).findIds()
            addPrajService(serviceId)
        }

        fun getPrajService()=mapPrajServiceUsed.get(serviceId)!!

        @Nested
        inner class `empty` {


            @Test
            fun `and run complete harvester then not consumer anything`() {
                harvesterOrchestrator.harvesterComplete()

                assertThat(harvesterConsumerList).hasSize(0)
            }

            @Test
            fun `and run complete harvester then call flux harvester one time`() {
                harvesterOrchestrator.harvesterComplete()

                verify(harvesterProcessor, times(1)).harvestCompleteFlux(getPrajService())
                verify(harvesterProcessor, never()).harvestFlux(any())
            }

            @Test
            fun `and run diff harvester then not consumer anything`() {
                harvesterOrchestrator.harvesterDiff()

                assertThat(harvesterConsumerList).hasSize(0)
            }

            @Test
            fun `and run diff harvester then call flux harvester one time`() {
                harvesterOrchestrator.harvesterDiff()

                verify(harvesterProcessor, times(1)).harvestFlux(getPrajService())
                verify(harvesterProcessor, never()).harvestCompleteFlux(any())
            }
        }


        @Nested
        inner class `with one harvested-update`{

            @BeforeEach
            fun before(){
                createAnswerForHarvesterProcessorUpdate().harvestCompleteFlux(any())
                createAnswerForHarvesterProcessorUpdate().harvestFlux(any())
            }

            @Test
            fun `and run complete harvester then consumer one element`(){
                harvesterOrchestrator.harvesterComplete()

                assertThat(harvesterConsumerList).hasSize(1)
            }

            @Test
            fun `and run diff harvester then consumer one element`(){
                harvesterOrchestrator.harvesterDiff()

                assertThat(harvesterConsumerList).hasSize(1)
            }

            @Test
            fun `and run complete harvester then call fluxHarvesterProcessor complete one time`(){
                harvesterOrchestrator.harvesterComplete()

                verify(harvesterProcessor, times(1)).harvestCompleteFlux(getPrajService())
                verify(harvesterProcessor, never()).harvestFlux(any())
            }

            @Test
            fun `and run diff harvester then call fluxHarvesterProcessor diff one time`(){
                harvesterOrchestrator.harvesterDiff()

                verify(harvesterProcessor, times(1)).harvestFlux(getPrajService())
                verify(harvesterProcessor, never()).harvestCompleteFlux(any())
            }

        }

        @Nested
        inner class `with two harvested-update and one harvested-delete`{

            fun myCreateAnswerForHarvesterProcessor() = createAnswerForHarvesterProcessor(HarvestedOp.UPDATED, HarvestedOp.UPDATED, HarvestedOp.DELETED)

            @BeforeEach
            fun before(){
                myCreateAnswerForHarvesterProcessor().harvestCompleteFlux(any())
                myCreateAnswerForHarvesterProcessor().harvestFlux(any())
            }

            @Test
            fun `and run complete harvester then consumer 3 elements`(){
                harvesterOrchestrator.harvesterComplete()

                assertThat(harvesterConsumerList).hasSize(3)
            }

            @Test
            fun `and run diff harvester then consumer 3 elements`(){
                harvesterOrchestrator.harvesterDiff()

                assertThat(harvesterConsumerList).hasSize(3)
            }

            @Test
            fun `and run complete harvester then consumer has two updates and one delete`(){
                harvesterOrchestrator.harvesterComplete()

                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_0.md"}).isNotNull
                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_0.md"}?.op).isEqualTo(HarvestedOp.UPDATED)

                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_1.md"}).isNotNull
                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_1.md"}?.op).isEqualTo(HarvestedOp.UPDATED)

                assertThat(harvesterConsumerList.find{it.doc.path == "D_test_2.md"}).isNotNull
                assertThat(harvesterConsumerList.find{it.doc.path == "D_test_2.md"}?.op).isEqualTo(HarvestedOp.DELETED)
            }

            @Test
            fun `and run diff harvester then consumer has two updates and one delete`(){
                harvesterOrchestrator.harvesterDiff()

                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_0.md"}).isNotNull
                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_0.md"}?.op).isEqualTo(HarvestedOp.UPDATED)

                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_1.md"}).isNotNull
                assertThat(harvesterConsumerList.find{it.doc.path == "U_test_1.md"}?.op).isEqualTo(HarvestedOp.UPDATED)

                assertThat(harvesterConsumerList.find{it.doc.path == "D_test_2.md"}).isNotNull
                assertThat(harvesterConsumerList.find{it.doc.path == "D_test_2.md"}?.op).isEqualTo(HarvestedOp.DELETED)
            }
        }


        @Nested
        inner class `with random harvested operations` {

            val harvestedOperations = createRandomHarvestedOperations()


            fun createRandomHarvestedOperations(): Array<HarvestedOp> {
                val randomQtd = 20 + Random().nextInt(30)
                return (0..randomQtd - 1).map { HarvestedOp.random() }.toTypedArray()
            }

            @BeforeEach
            fun before() {
                createAnswer().harvestCompleteFlux(any())
                createAnswer().harvestFlux(any())
            }

            private fun createAnswer(): FluxHarvesterProcessor = doAnswer {
                val prajService = it.arguments[0] as PrajService
                val harvesteds = harvestedOperations
                        .mapIndexed() { i, a -> HarvestedFixture.create(a, prajService.name, i) }
                Flux.just(*harvesteds.toTypedArray())
            }.whenever(harvesterProcessor)


            @Test
            fun `and run complete harvester then consumer has all operations`() {
                harvesterOrchestrator.harvesterComplete()

                assertThat(harvesterConsumerList).hasSize(harvestedOperations.size)

                assertThat(harvesterConsumerList.filter { it.op == HarvestedOp.UPDATED }.size).isEqualTo(harvestedOperations.filter { it == HarvestedOp.UPDATED }.size)
                assertThat(harvesterConsumerList.filter { it.op == HarvestedOp.DELETED }.size).isEqualTo(harvestedOperations.filter { it == HarvestedOp.DELETED }.size)
                assertThat(harvesterConsumerList.filter { it.op == HarvestedOp.NO_OP }.size).isEqualTo(harvestedOperations.filter { it == HarvestedOp.NO_OP }.size)

                assertThat(harvesterConsumerList).allMatch { it.doc.path.matches(Regex("${it.op.code}_${serviceId}_\\d+\\.md")) }
            }
        }
    }

    @Nested
    inner class `has N harvesterProcessor`{

        @BeforeEach
        fun before(){
            createAnswerForHarvesterProcessorUpdate().harvestCompleteFlux(any())
            createAnswerForHarvesterProcessorUpdate().harvestFlux(any())
        }

        val STR_N_SERVICE_ID = "test_%02d"

        fun getPrajService(serviceId:Int)=mapPrajServiceUsed.get(STR_N_SERVICE_ID.format(serviceId))!!

        @Nested
        inner class `with one harvested-update`{

            fun createPrajServices(serviceQtd:Int){
                val list = (1..serviceQtd)
                        .map{ STR_N_SERVICE_ID.format(it)}
                        .map {
                            addPrajService(it)
                            it
                        }
                        .toList()
                doReturn(list).whenever(prajServiceDAO).findIds()
            }

            @ParameterizedTest(name = "run #{index} with tag [{arguments}]")
            @ValueSource(ints = intArrayOf(2, 10, 15, 23, 44))
            fun `and run complete harvester then consumer one element and call fluxHarvesterProcessor complete n times`(serviceQtd:Int){
                createPrajServices(serviceQtd)

                prajServiceDAO.findIds().map{prajServiceDAO.findById(it)}.map{it.orElse(PrajService())}.map{it.name}

                harvesterOrchestrator.harvesterComplete()

                assertThat(harvesterConsumerList).hasSize(serviceQtd)
                argumentCaptor<PrajService>().apply{
                    verify(harvesterProcessor, atLeastOnce()).harvestCompleteFlux(capture())
                    assertThat(allValues).hasSize(serviceQtd)
                    assertThat(allValues).containsAll(mapPrajServiceUsed.values)
                }

                verify(harvesterProcessor, never()).harvestFlux(any())
            }

            @ParameterizedTest(name = "run #{index} with tag [{arguments}]")
            @ValueSource(ints = intArrayOf(2, 10, 15, 23, 44))
            fun `and run diff harvester then consumer one element and call fluxHarvesterProcessor diff n times`(serviceQtd:Int){
                createPrajServices(serviceQtd)

                prajServiceDAO.findIds().map{prajServiceDAO.findById(it)}.map{it.orElse(PrajService())}.map{it.name}

                harvesterOrchestrator.harvesterDiff()

                assertThat(harvesterConsumerList).hasSize(serviceQtd)
                argumentCaptor<PrajService>().apply{
                    verify(harvesterProcessor, atLeastOnce()).harvestFlux(capture())
                    assertThat(allValues).hasSize(serviceQtd)
                    assertThat(allValues).containsAll(mapPrajServiceUsed.values)
                }

                verify(harvesterProcessor, never()).harvestCompleteFlux(any())
            }
        }
    }

    private fun createAnswerForHarvesterProcessorUpdate() =createAnswerForHarvesterProcessor(HarvestedOp.UPDATED)
    private fun createAnswerForHarvesterProcessor(vararg op:HarvestedOp)
            = doAnswer { createFlux(it, *op) }.whenever(harvesterProcessor)
}