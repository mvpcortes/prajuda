package br.uff.mvpcortes.prajuda.config

import br.uff.mvpcortes.prajuda.workdir.WorkDirectoryProviderImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WorkerConfiguration {

    @Bean
    fun workerDirectoryProvider() = WorkDirectoryProviderImpl()
}