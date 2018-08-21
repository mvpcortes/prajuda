package br.uff.mvpcortes.prajuda.service.config

import br.uff.mvpcortes.prajuda.util.FileUtils
import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import org.springframework.context.annotation.Primary
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.io.File

@Primary
@Service
class WorkDirectoryProviderTestImpl : WorkDirectoryProvider{

    private var workDir:File=FileUtils.createTempDirectory("temp_dir_test_")

    override fun workDirectory()= workDir


    @EventListener
    fun onStartedEvent(event: ContextStartedEvent) {
        workDir.tryDeleteRecursively()
        workDir = FileUtils.createTempDirectory("temp_dir_test_")
    }

    @EventListener
    fun onClosedEvent(event: ContextClosedEvent) {
        workDir .tryDeleteRecursively()
    }


}