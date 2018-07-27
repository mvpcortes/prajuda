package br.uff.mvpcortes.prajuda.dao.highapi

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@ConfigurationProperties(prefix="elasticsearch")
class ESHighApiProperties (

){

    var highapi:Map<String, Host> = mutableMapOf()

    class Host {
        var host: String? = null
        var port: Int? = null
        var mode: String? = "http"

    }

    fun listHosts()=highapi.entries.asSequence()
            .sortedBy { it.key }
            .map{ it.value }
            .toList()
            .let {
                if(!it.isEmpty()) it
                else throw IllegalStateException("Prajuda need at least one ES host to init")
            }
}