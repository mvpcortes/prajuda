package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.harvester.SimpleFluxHarvesterProcessor
import br.uff.mvpcortes.prajuda.service.config.ConfigService


@org.springframework.stereotype.Service
class GitFluxHarvesterProcessor(configService: ConfigService): SimpleFluxHarvesterProcessor(GitHarvesterProcessor(configService))