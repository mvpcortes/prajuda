package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean



@Configuration
class PrajudaJdbcConfiguration {

//    @Bean
//    fun messageSource(): ResourceBundleMessageSource {
//        return   ResourceBundleMessageSource()
//                .let { it.setBasename("i18n/messages"); it }
//                .let { it.setDefaultEncoding("UTF-8");it }
//                .let { it.setUseCodeAsDefaultMessage(false);it }
//                .let { it.setFallbackToSystemLocale(false);it }
//    }
//
//    @Bean
//    fun validator(messageSource: ResourceBundleMessageSource): LocalValidatorFactoryBean {
//        val bean = LocalValidatorFactoryBean()
//        bean.setValidationMessageSource(messageSource)
//        return bean
//    }
}