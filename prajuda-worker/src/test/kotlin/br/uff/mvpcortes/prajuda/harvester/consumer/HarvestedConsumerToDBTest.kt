package br.uff.mvpcortes.prajuda.harvester.consumer

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.harvester.HarvestedFixture
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The HarvestedConsumerToDB")
internal class HarvestedConsumerToDBTest{

    val prajDocumentDAO: PrajDocumentDAO = mock{

    }

    val harvestedConsumerToDB = HarvestedConsumerToDB(prajDocumentDAO)

    @Test
    fun `when consumer no_op then do nothing`(){

        val harvested = HarvestedFixture.noop(id=1)

        harvestedConsumerToDB.accept(harvested)

        inOrder(prajDocumentDAO){
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `when consumer up then do update traking`(){

        val harvested = HarvestedFixture.update(id=1)

        harvestedConsumerToDB.accept(harvested)

        inOrder(prajDocumentDAO){
            verify(prajDocumentDAO).saveTrackingServiceAndPath(harvested.doc)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `when consumer delete then do update traking`(){

        val harvested = HarvestedFixture.delete(id=1)

        harvestedConsumerToDB.accept(harvested)

        inOrder(prajDocumentDAO){
            verify(prajDocumentDAO).deleteTrackingServiceAndPath(harvested.doc)
            verifyNoMoreInteractions()
        }
    }
}