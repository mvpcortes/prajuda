package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedConsumer
import br.uff.mvpcortes.prajuda.harvester.HarvesterProcessor
import br.uff.mvpcortes.prajuda.harvester.exception.InvalidRepositoryFormatException
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.service.config.ConfigService
import br.uff.mvpcortes.prajuda.util.tryDeleteRecursively
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.slf4j.Logger
import java.io.File
import java.time.Instant
import java.time.LocalDateTime


@org.springframework.stereotype.Service
class GitHarvesterProcessor(val configService: ConfigService): HarvesterProcessor {

    private val logger: Logger = loggerFor(GitHarvesterProcessor::class)

    override fun harvest(service: PrajService, blockDeal: HarvestedConsumer) {
        TODO("make it")
    }

    override fun harvestComplete(service: PrajService, blockDeal:HarvestedConsumer) {
        val dirBase = configService.getWorkDirectoryForHarvester("git_harvester")
        val dirService = File(dirBase, service.name)

        dirService.tryDeleteRecursively()
        dirService.mkdir()

        Git.cloneRepository().setURI(service.repositoryInfo.uri).setRemote("origin").setDirectory(dirService).call().close()

        FileRepository(File(dirService, ".git")).use{
            repository->

            val refTag =findLastTag(repository, service.repositoryInfo.branch)
            ?: throw InvalidRepositoryFormatException("Repository ${service.repositoryInfo.uri} does not have a valid tag in ${service.repositoryInfo.branch} branch")

            logger.info("checkouting tag ${refTag.second}(${refTag.first})")
            //checkout tag
            Git(repository).checkout().setName(refTag.second).call()
            val dirPrajuda = File(dirService, service.documentDir)
            if(!dirPrajuda.exists() || !dirPrajuda.isDirectory) {
                throw InvalidRepositoryFormatException("Repository ${service.repositoryInfo.uri} does not have directory '${service.documentDir}'")
            }

            dirPrajuda.walkTopDown()
                    .asSequence()
                    .filter{it.isFile}
                    .map{ logInfo(it) }
                    .map{ createPrajDocument(dirPrajuda, it, refTag.second, refTag.first, service) }
                    .map { Harvested(HarvesterProcessor.HarvestedOp.UPDATED, it) }
                    .forEach(blockDeal)

            }
        }

    private fun logInfo(it: File): File {
        logger.info("harvesting ${it.name}")
        return it
    }

    private fun createPrajDocument(dirPrajuda:File, it: File, tagName:String, revCommit:RevCommit, service: PrajService): PrajDocument {
        val tagDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(revCommit.commitTime.toLong()),
                revCommit.authorIdent.timeZone.toZoneId())

        return PrajDocument(
                content = it.readText(),
                tag = tagName,
                path = it.relativeTo(dirPrajuda).toString(),
                serviceId = service.id,
                serviceName = service.name
        )
    }


    /**
     * @see Repository.peel magic = https://stackoverflow.com/a/27173024/8313595
     */
    private fun getSafePeeledObjectId(repository:Repository, ref:Ref):ObjectId=  repository.peel(ref)
            ?.peeledObjectId
            ?:ref.objectId


    private fun findLastTag(repository:Repository, branchName:String):Pair<RevCommit, String>? {

        logger.info("{}", repository.directory)
        val tags = Git(repository).tagList().call()
                .asSequence()
                .associate { Pair(getSafePeeledObjectId(repository, it), it.name.replace("refs/tags/", "")) }

        logger.info("tags:{}", tags)

        val refBranch: Ref = repository.exactRef("refs/heads/$branchName")

        logger.info("branch: {}", refBranch.objectId)

        return RevWalk(repository).use { revWalk ->
            val revCommit = revWalk.parseCommit(refBranch.objectId)
            revWalk.markStart(revCommit)

            revWalk.asSequence()
                    .map { logger.info("xuxu {} - {}",it, it.toObjectId()); it}
                    .map { element -> Pair(element, tags[element])}
                    .filter{pair->pair.second !== null}
                    .map{pair->Pair(pair.first, pair.second!!)}
                    .firstOrNull()
         }
    }
}