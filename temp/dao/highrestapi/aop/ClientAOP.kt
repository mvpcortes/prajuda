package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.aop

import br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi.ClientManager
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired

@Aspect
class ClientAOP(clientManager:ClientManager){

    @Autowired
    private val clientManager:ClientManager = clientManager

    @Throws(Throwable::class)
    @Around("@annotation(ClientConnection)")
    fun connection(pjp: ProceedingJoinPoint){
        clientManager.runTemplate (pjp::proceed)
    }
}