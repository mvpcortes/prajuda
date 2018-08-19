package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * This class will generate recommendations looking at user context, service querying or/and manual scheduling service
 */
@Service
class RecommendationService {

    @Autowired
    lateinit var prajServiceDAO:PrajServiceDAO

    fun recommendServices()= prajServiceDAO.findByIds("1", "10", "79", "80", "64", "88")
}
