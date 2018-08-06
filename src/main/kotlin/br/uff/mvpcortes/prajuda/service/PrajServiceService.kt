package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PrajServiceService(val prajServiceDAO:PrajServiceDAO) {

    @Transactional
    fun save(prajService: PrajService)=prajServiceDAO.save(prajService)

}