package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Test

class PrajServiceServiceTest{

    val prajServiceDAO : PrajServiceDAO = mock{}

    var prajServiceService = PrajServiceService(prajServiceDAO)

    @Test
    fun `when save then delegate to DAO`(){
        val prajService = PrajServiceFixture.withName("a")
        prajServiceService.save(prajService)

        verify(prajServiceDAO).save(prajService)
    }



    @Test
    fun `when findById then delegate to DAO`(){
        val prajService = PrajServiceFixture.withName("a")
        prajServiceService.findById("1")

        verify(prajServiceDAO).findByIdNullable("1")
    }


    @Test
    fun `when findServices then delegate to DAO`(){
        val prajService = PrajServiceFixture.withName("a")
        prajServiceService.findServices(0, 10)

        verify(prajServiceDAO).findPage(0, 10)
    }

    @Test
    fun `when count then delegate to DAO`(){
        val prajService = PrajServiceFixture.withName("a")
        prajServiceService.count()

        verify(prajServiceDAO).count()
    }

    @Test
    fun `when findByName then delegate to DAO`(){
        val prajService = PrajServiceFixture.withName("a")
        prajServiceService.findByName(prajService.name)

        verify(prajServiceDAO).findByName(prajService.name)
    }

}