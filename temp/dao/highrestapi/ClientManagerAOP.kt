package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cglib.proxy.MethodInterceptor
import org.springframework.cglib.proxy.MethodProxy
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Component
class ClientManagerAOP (clientManager:ClientManager): MethodInterceptor {

    @Autowired
    val clientManager:ClientManager = clientManager

    @Throws(Throwable::class)
    override fun intercept(objTarget: Any, method: Method, objArgs: Array<Any>, methodProxy: MethodProxy): Any? =

        clientManager.run {
            methodProxy.invoke(objTarget, objArgs)
        }
}
