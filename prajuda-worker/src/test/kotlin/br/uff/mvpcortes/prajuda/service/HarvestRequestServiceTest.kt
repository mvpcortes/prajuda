package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.config.WorkerProperties
import br.uff.mvpcortes.prajuda.dao.HarvestRequestDAO
import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.harvester.FluxHarvesterProcessor
import br.uff.mvpcortes.prajuda.harvester.HarvestedFixture
import br.uff.mvpcortes.prajuda.harvester.consumer.SaveHarvestedDB
import br.uff.mvpcortes.prajuda.model.HarvestType
import br.uff.mvpcortes.prajuda.model.fixture.HarvesterRequestFixture
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import br.uff.mvpcortes.prajuda.workdir.WorkDirectoryService
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@DisplayName("A HarvestRequestService")
class HarvestRequestServiceTest{

    val workerProperties = WorkerProperties(delayForHarvestRequest = 1, threadCount = 1)

    val harvestRequestDAO : HarvestRequestDAO = mock{
        on{it.getAndStartOldOpen(anyInt())}.thenReturn(emptyList())
    }

    val harvestedConsumer: SaveHarvestedDB = mock {  }

    val harvesterTypeService : HarvesterTypeService = mock { }

    val prajServiceDAO : PrajServiceDAO = mock{ }

    val workDirectoryService : WorkDirectoryService = mock{}

    val harvestRequestService=HarvestRequestService(
            harvestRequestDAO=harvestRequestDAO,
            workerProperties = workerProperties,
            harvestedConsumer = harvestedConsumer,
            harvesterTypeService = harvesterTypeService,
            prajServiceDAO =  prajServiceDAO,
            workDirectoryService = workDirectoryService)


    @Test
    fun `when not get request from DAO then return empty flux`(){
        StepVerifier.create(harvestRequestService.fluxOpenHarvesters())
                .expectNextCount(0)
                .verifyComplete()

        verify(harvestRequestDAO, times(1)).getAndStartOldOpen(workerProperties.maxHarvestRequest())
    }

    @Test
    fun `when  get one request from DAO then return flux with one element`(){
        val harvestRequest = HarvesterRequestFixture.started().copy(id="XUXU")
        doReturn(listOf(harvestRequest)).whenever(harvestRequestDAO).getAndStartOldOpen(anyInt())

        StepVerifier.create(harvestRequestService.fluxOpenHarvesters())
                .assertNext{
                    @Suppress("UnusedEquals")
                    "XUXU" != it.id
                }
                .expectComplete()
                .verify()

        verify(harvestRequestDAO, times(1)).getAndStartOldOpen(workerProperties.maxHarvestRequest())
    }

    @Nested
    inner class  `with a request` {
        val harvestRequest = HarvesterRequestFixture.open()

        val prajService = PrajServiceFixture.withName("XUXU")

        val harvestedUpdate  = HarvestedFixture.update("file", 1)

        val fluxHarvesterProcessor:FluxHarvesterProcessor = mock{
            on { harvestTyped(HarvestType.COMPLETE, prajService) }.thenReturn(Flux.just(
                    harvestedUpdate
            ))
        }

        @BeforeEach
        fun init(){
            doReturn(listOf(harvestRequest))
                    .whenever(harvestRequestDAO)
                    .getAndStartOldOpen(anyInt())

            doReturn (prajService)
                    .whenever(prajServiceDAO)
                    .findByIdNullable(anyString())

            doReturn(fluxHarvesterProcessor)
                    .whenever(harvesterTypeService)
                    .getHarvesterProcessor(anyString())
        }

        @Test
        fun `when  get one request from DAO then return flux with one element`(){
            StepVerifier.create(harvestRequestService.fluxHarvested())
                    .assertNext{it.first == harvestRequest && it.second == harvestedUpdate}
                    .expectComplete()
                    .verify()

            inOrder(fluxHarvesterProcessor, harvestRequestDAO,  harvestedConsumer, harvesterTypeService,  prajServiceDAO){
                verify(harvestRequestDAO).getAndStartOldOpen(workerProperties.maxHarvestRequest())
                verify(prajServiceDAO).findByIdNullable(harvestRequest.serviceSourceId)
                verify(harvesterTypeService).getHarvesterProcessor(prajService.harvesterTypeId)
                verify(fluxHarvesterProcessor).harvestTyped(harvestRequest.harvestType, prajService)
                verifyNoMoreInteractions()
            }
        }

        @Test
        fun `when  get 3 requests from DAO then return flux with one element`(){
            StepVerifier.create(harvestRequestService.fluxHarvested())
                    .assertNext{it.first == harvestRequest && it.second == harvestedUpdate}
                    .expectComplete()
                    .verify()

            inOrder(fluxHarvesterProcessor, harvestRequestDAO,  harvestedConsumer, harvesterTypeService,  prajServiceDAO){
                verify(harvestRequestDAO).getAndStartOldOpen(workerProperties.maxHarvestRequest())
                verify(prajServiceDAO).findByIdNullable(harvestRequest.serviceSourceId)
                verify(harvesterTypeService).getHarvesterProcessor(prajService.harvesterTypeId)
                verify(fluxHarvesterProcessor).harvestTyped(harvestRequest.harvestType, prajService)
                verifyNoMoreInteractions()
            }
        }
    }

}