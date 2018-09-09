package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import reactor.test.StepVerifier

@ExtendWith(value=[SpringExtension::class])
@SpringBootTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RecommendationServiceTest{

    @Autowired
    lateinit var prajServiceDAO: PrajServiceDAO

    @Autowired
    lateinit var recommendationService:RecommendationService

    @Transactional
    @Rollback
    @Test
    fun `when get recomended services and without services then return nothing`(){
        prajServiceDAO.findIds().asSequence().forEach{ prajServiceDAO.delete(it) }

        StepVerifier.create(recommendationService.recommendServices())
                .verifyComplete()
    }
}