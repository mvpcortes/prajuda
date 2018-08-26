package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.harvester.DefineHarvester
import br.uff.mvpcortes.prajuda.harvester.SimpleFluxHarvesterProcessor
import br.uff.mvpcortes.prajuda.workdir.WorkDirectoryService


@org.springframework.stereotype.Service
@DefineHarvester(name="Git (Classic)", id= GitFluxHarvesterProcessor.STR_HARVESTER_ID)
class GitFluxHarvesterProcessor(configService: WorkDirectoryService): SimpleFluxHarvesterProcessor(GitHarvesterProcessor(configService)){
    companion object {
        const val STR_HARVESTER_ID = "git_classic"
    }
}