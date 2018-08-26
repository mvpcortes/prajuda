package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.model.fixture.HarvesterRequestFixture
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("A HarvestRequestJDBCDAO")
class HarvestRequestJDBCDAOTest{

    @Autowired
    lateinit var harvestRequestJDBCDAO: HarvestRequestJDBCDAO

    @Test
    @Transactional
    @Rollback
    fun `when not there is an opening harvestRequest then return empty`(){
        harvestRequestJDBCDAO.deleteAll()

//        val request = harvestRequestJDBCDAO.save(HarvesterRequestFixture.open())

        val list = harvestRequestJDBCDAO.getOldOpen(1)

        assertThat(list).isEmpty()
    }

    @Test
    @Transactional
    @Rollback
    fun `when there is an opening harvestRequest then getOldOpen return one`(){
        harvestRequestJDBCDAO.deleteAll()

        val request = harvestRequestJDBCDAO.save(HarvesterRequestFixture.open())

        val list = harvestRequestJDBCDAO.getOldOpen(1)

        assertThat(list).isNotEmpty

        val savedRequest = list[0]

        //we can use equals because is a data class and the equals function compare all properties
        assertThat(savedRequest).isEqualTo(request)
    }

    @Test
    @Transactional
    @Rollback
    fun `when there is an started harvestRequest then return empty`(){
        harvestRequestJDBCDAO.deleteAll()

        harvestRequestJDBCDAO.save(HarvesterRequestFixture.started())

        val list = harvestRequestJDBCDAO.getOldOpen(1)

        assertThat(list).isEmpty()
    }

    @Test
    @Transactional
    @Rollback
    fun `when there is an completed harvestRequest then return empty`(){
        harvestRequestJDBCDAO.deleteAll()

        harvestRequestJDBCDAO.save(HarvesterRequestFixture.completed())

        val list = harvestRequestJDBCDAO.getOldOpen(1)

        assertThat(list).isEmpty()
    }

    @Test
    @Transactional
    fun `when getAndStart ask for negative elements then fail`(){
        assertThatIllegalArgumentException().
                isThrownBy {
                harvestRequestJDBCDAO.getAndStartOldOpen(-1)
                }
                .withMessage("Cannot get negative qtd")
    }

    @Test
    @Rollback
    @Transactional
    fun `when getAndStart ask for zero elements then return empty`(){
        harvestRequestJDBCDAO.save(HarvesterRequestFixture.open())

        val list = harvestRequestJDBCDAO.getAndStartOldOpen(0)

        assertThat(list).isEmpty()
    }

    @Test
    @Rollback
    @Transactional
    fun `when getAndStart a request then change in database`(){
        harvestRequestJDBCDAO.deleteAll()

        val request = harvestRequestJDBCDAO.save(HarvesterRequestFixture.open())

        val list = harvestRequestJDBCDAO.getAndStartOldOpen(1)

        assertThat(list).hasSize(1)

        list[0].let{savedRequest->

            //
            assertThat(savedRequest.startedAt).isNotNull()
            assertThat(savedRequest.startedAt).isAfterOrEqualTo(savedRequest.createAt)

            assertThat(savedRequest).isNotEqualTo(request)

            assertThat(savedRequest.id).isEqualTo(request.id)
            assertThat(savedRequest.createAt).isEqualTo(request.createAt)
            assertThat(savedRequest.startedAt).isNotEqualTo(request.startedAt)

            assertThat(savedRequest.startedAt).isAfterOrEqualTo(savedRequest.createAt)

            //
            savedRequest
        }

    }

    @Test
    @Rollback
    @Transactional
    fun `when getAndStart a request and do another getAndStart return empty`(){
        harvestRequestJDBCDAO.deleteAll()

        harvestRequestJDBCDAO.save(HarvesterRequestFixture.open())

        val list = harvestRequestJDBCDAO.getAndStartOldOpen(1)

        assertThat(list).hasSize(1)

        val newList = harvestRequestJDBCDAO.getAndStartOldOpen(1)

        assertThat(newList).isEmpty()

    }

    @Test
    fun `when concat datetime and one id then return a array with two elements`(){

        val time = LocalDateTime.now()
        val ids = listOf("1")

        val newArray = HarvestRequestJDBCDAO.toArray(time, ids)

        assertThat(newArray).containsExactly(time, "1")
    }

    @Test
    fun `when concat datetime and two id then return a array with two elements`(){

        val time = LocalDateTime.now()
        val ids = listOf("1", "2")

        val newArray = HarvestRequestJDBCDAO.toArray(time, ids)

        assertThat(newArray).containsExactly(time, "1", "2")
    }


    @Test
    @Transactional
    @Rollback
    fun `when completeRequests of valid request then update datetime`(){
        harvestRequestJDBCDAO.deleteAll()

        val request = harvestRequestJDBCDAO.save(HarvesterRequestFixture.started())

        harvestRequestJDBCDAO.completeRequests(LocalDateTime.now(), listOf(request.id!!))

        val requestUpdated = harvestRequestJDBCDAO.findById(request.id!!)!!

        assertThat(requestUpdated.id).isEqualTo(request.id)
        assertThat(requestUpdated.completedAt).isNotNull()
        assertThat(requestUpdated.completedAt).isAfterOrEqualTo(request.startedAt)
    }

    @Test
    @Transactional
    @Rollback
    fun `when completeRequests with zero qtd then return empty`(){
        harvestRequestJDBCDAO.deleteAll()

        harvestRequestJDBCDAO.save(HarvesterRequestFixture.completed())

        val list = harvestRequestJDBCDAO.completeRequests(LocalDateTime.now(), emptyList())

        assertThat(list).isEqualTo(0)
    }
}