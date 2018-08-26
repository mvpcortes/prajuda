package br.uff.mvpcortes.prajuda.config

import br.uff.mvpcortes.prajuda.workdir.WorkDirectoryProviderTestImpl
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class WorkerTestConfiguration {

    @Bean
    fun workerDirectoryProvider() = WorkDirectoryProviderTestImpl()
}