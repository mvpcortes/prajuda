package br.uff.mvpcortes.prajuda.dao.highapi

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
@ConfigurationProperties(prefix="elasticsearch")
class ESHighApiProperties (

){

    lateinit var highapi:Map<String, Host>

    class Host {
        var host: String? = null
        var port: Int? = null
        var mode: String? = "http"

    }

    fun listHosts()=highapi?.entries.asSequence().sortedBy { it.key }.map{it.value}.toList()
}