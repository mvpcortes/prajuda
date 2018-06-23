package br.uff.mvpcortes.prajuda.daoprototype.dao.highrestapi

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HighRestApiConfig{

    @Qualifier("highRestApiObjectMapper")
    @Bean
    fun highRestApiObjectMapper():ObjectMapper{
        return ObjectMapper()
    }

}