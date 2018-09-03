package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PrajServiceService(val prajServiceDAO:PrajServiceDAO) {

    @Transactional
    fun save(prajService: PrajService)=prajServiceDAO.save(prajService)

    /**
     * I dont know if should wrap by mono/flux. But it will turn harder thymeleaf render
     */
    fun findById(id: String) =  prajServiceDAO.findByIdNullable(id)

    fun findServices(page: Int, pageSize: Int): Flux<PrajService> {
        return prajServiceDAO.findPage(page, pageSize)
    }

    fun count(): Long= prajServiceDAO.count()

    fun findByName(serviceName: String)=prajServiceDAO.findByName(serviceName.toLowerCase())


}