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


@org.springframework.stereotype.Service
class GitHarvesterProcessor(val configService: ConfigService): HarvesterProcessor {

    companion object {
        const val STR_GIT_HARVESTER_ID = "git_harvester"
    }

    private val logger: Logger = loggerFor(GitHarvesterProcessor::class)

    private class MyRefTag(val name:String, val ref:RevCommit)

    override fun harvest(service: PrajService, blockDeal: (Harvested)->Unit) {

        val dirService = getWorkDirectory(service)

        if (!repositoryExists(dirService)) {
            throw NonClonedRepositoryException(service, dirService)
        }

        if(service.repositoryInfo.lastTag == null){
            logger.warn("There is not lastTag on service.repositoryInfo. run complete harvester")
            this.harvestComplete(service, blockDeal)
            return
        }

        FileRepository(File(dirService, ".git")).use { repository ->

            //pulling repository
            logger.debug("pulling cache (local repository)")
            Git(repository).checkout().setName(service.repositoryInfo.branch).call()
            Git(repository).pull().call()

            val actualTagName = checkoutTag(repository, service).name

            //@see https://gist.github.com/steos/805992
            val actualRefCommit = resolveCommit(repository, actualTagName)?: throw IllegalStateException("Cannot found actual tag '$actualTagName'")
            val actualTag = actualRefCommit.tree

            val previousTagName = service.repositoryInfo.lastTag

            val previousRefCommit = resolveCommit(repository, previousTagName)?: throw IllegalStateException("Cannot found previous tag '$previousTagName'")
            val previousTag = previousRefCommit.tree


           val prajudaDir = getSafePrajudaDir(dirService, service)

            repository.newObjectReader().use{objectReader->
                val oldTreeIter =createCanonicalTreeParser(objectReader,  previousTag)
                val newTreeIter =createCanonicalTreeParser(objectReader,  actualTag)

                Git(repository).use { git ->
                    getDiff(git, newTreeIter, oldTreeIter)
                            .asSequence()
                            .flatMap { toPrajDocumentDiff(actualRefCommit, actualTagName, service, it, prajudaDir).asSequence()}
                            .forEach { blockDeal(it) }
                }
            }
        }
    }

    private fun createCanonicalTreeParser(or: ObjectReader, revTree: RevTree):CanonicalTreeParser{
        val oldTreeIter = CanonicalTreeParser()
        oldTreeIter.reset(or,revTree)
        return oldTreeIter

    }

    private fun resolveCommit(repository:FileRepository, actualTagName: String)=repository.parseCommit(repository.resolve("refs/tags/$actualTagName"))

    private fun getDiff(git: Git, newTreeIter: CanonicalTreeParser, oldTreeIter: CanonicalTreeParser)= git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .call()

    private fun toPrajDocumentDiff(revCommit:RevCommit, tagName:String, service:PrajService, entry: DiffEntry, dirPrajuda:File):
            Array<Harvested> =
            when(entry.changeType){
            /** Add/modifying/copy a new file to the project  */
                DiffEntry.ChangeType.ADD, DiffEntry.ChangeType.MODIFY,DiffEntry.ChangeType.COPY -> {
                    checkPath(entry.newPath, service, { createUpdatedHarvested(revCommit, dirPrajuda, entry, tagName, service) })
                }

            /** Delete an existing file from the project  */
                DiffEntry.ChangeType.DELETE->
                    checkPath(entry.oldPath, service, {Harvested(HarvestedOp.DELETED, createDeletedPrajDocument(dirPrajuda, service, entry))})
            /** Rename an existing file to a new location  */
            //make a delete and a update
                DiffEntry.ChangeType.RENAME->
                        checkPath(entry.oldPath, service, {Harvested(HarvestedOp.DELETED, createDeletedPrajDocument(dirPrajuda, service, entry))})
                        .plus(
                            checkPath(entry.newPath, service, {createUpdatedHarvested(revCommit, dirPrajuda, entry, tagName, service)})
                        )
                else -> emptyArray()
            }

    private fun checkPath(path: String, service:PrajService,function: () -> Harvested) =
    if(path.startsWith(service.documentDir) && acceptPath(path)){
        arrayOf(function())
    }else{
        emptyArray()
    }

    private fun createUpdatedHarvested(revCommit:RevCommit, dirPrajuda: File, entry: DiffEntry, tagName: String,  service: PrajService): Harvested {
        return Harvested(HarvestedOp.UPDATED,
                createPrajDocument(dirPrajuda,
                        service.removeDocumentDir(entry.newPath),
                        tagName,
                        revCommit,
                        service))
    }

    private fun createDeletedPrajDocument(prajudaFile:File, service:PrajService, entry: DiffEntry)=
            PrajDocument(
                    content = "",
                    tag = "",
                    path = File(service.removeDocumentDir(entry.oldPath)).toString(),
                    serviceId = service.id,
                    serviceName = null
            )



    /**
     * @see [https://stackoverflow.com/a/12729335/8313595]
     */
    private fun repositoryExists(dirService: File): Boolean {
        return try {
            FileRepository(File(dirService, ".git")).use{it.objectDatabase.exists()}
        } catch (e: IOException) {
            logger.warn("check repo exists fail: {}", e.message, e)
            false
        }
    }

    override fun harvestComplete(service: PrajService, blockDeal: (Harvested)->Unit) {
        val dirService = getWorkDirectory(service)

        dirService.tryDeleteRecursively()
        dirService.mkdir()

        Git.cloneRepository().setURI(service.repositoryInfo.uri).setRemote("origin").setDirectory(dirService).call().close()

        FileRepository(File(dirService, ".git")).use{
            repository->

            val revTag = checkoutTag(repository, service)

            val dirPrajuda = getSafePrajudaDir(dirService, service)

            dirPrajuda.walkTopDown()
                    .asSequence()
                    .filter{it.isFile}
                    .map{it.relativeTo(dirPrajuda).toString()}
                    .filter{acceptPath(it)}
                    .map{ createPrajDocument(dirPrajuda, it, revTag.name, revTag.ref, service) }
                    .map { Harvested(HarvestedOp.UPDATED, it) }
                    .forEach(blockDeal)

        }
    }

    private fun getSafePrajudaDir(dirService: File, service: PrajService): File {
        val dirPrajuda = File(dirService, service.documentDir)
        if (!dirPrajuda.exists() || !dirPrajuda.isDirectory) {
            throw InvalidRepositoryFormatException("Repository '${service.repositoryInfo.uri}' does not have directory '${service.documentDir}'")
        }
        return dirPrajuda
    }

    private fun checkoutTag(repository: FileRepository, service: PrajService): MyRefTag =
            safeFindLastTag(repository, service).let {
                //checkout tag
                logger.info("checkouting tag ${it.name}(${it.ref})")
                Git(repository).checkout().setName(it.name).call()
                return it
            }

    private fun getWorkDirectory(service: PrajService): File = File(configService.getWorkDirectoryForHarvester(STR_GIT_HARVESTER_ID), service.name)


    private fun createPrajDocument(dirPrajuda:File, nameFile:String, tagName:String, revCommit:RevCommit, service: PrajService): PrajDocument {
        val tagDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(revCommit.commitTime.toLong()),
                revCommit.authorIdent.timeZone.toZoneId())

        return PrajDocument(
                content = File(dirPrajuda, nameFile).readText(),
                tag = tagName,
                path = nameFile,
                serviceId = service.id,
                serviceName = service.name
        )
    }


    /**
     * @see Repository.peel magic = https://stackoverflow.com/a/27173024/8313595
     */
    private fun getSafePeeledObjectId(repository:Repository, ref:Ref):ObjectId=  repository.refDatabase.peel(ref)
            ?.peeledObjectId
            ?:ref.objectId


    private fun safeFindLastTag(repository:Repository, service:PrajService) =
            findLastTag(repository, service.repositoryInfo.branch)
                    ?: throw InvalidRepositoryFormatException("Repository '${service.repositoryInfo.uri}' does not have a valid tag in '${service.repositoryInfo.branch}' branch")

    private fun findLastTag(repository:Repository, branchName:String):MyRefTag? {

        val tags =Git(repository)
                .tagList().call()
                .asSequence()
                .associate { Pair(getSafePeeledObjectId(repository, it), it.name.replace("refs/tags/", "")) }

        logger.debug("tags:{}", tags)

        val refBranch: Ref = repository.exactRef("refs/heads/$branchName")!!

        logger.debug("branch: {}", refBranch.objectId)

        return RevWalk(repository).use { revWalk ->
            val revCommit = revWalk.parseCommit(refBranch.objectId)
            revWalk.markStart(revCommit)

            revWalk.asSequence()
                    .map { element -> Pair(tags[element], element)}
                    .filter{pair->pair.first !== null}
                    .map{pair->MyRefTag(pair.first!!, pair.second)}
                    .firstOrNull()
        }
    }
}