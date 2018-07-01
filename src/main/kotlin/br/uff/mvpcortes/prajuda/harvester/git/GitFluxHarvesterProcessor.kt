package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.harvester.*
import br.uff.mvpcortes.prajuda.harvester.exception.InvalidRepositoryFormatException
import br.uff.mvpcortes.prajuda.harvester.exception.NonClonedRepositoryException
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.slf4j.Logger
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.io.IOException
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.*
import org.eclipse.jgit.revwalk.RevTree
import reactor.core.publisher.Flux


@org.springframework.stereotype.Service
class GitFluxHarvesterProcessor(val configService: ConfigService): SimpleFluxHarvesterProcessor(GitHarvesterProcessor(configService))