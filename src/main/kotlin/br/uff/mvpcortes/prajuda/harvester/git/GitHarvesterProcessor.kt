package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedConsumer
import br.uff.mvpcortes.prajuda.harvester.HarvesterProcessor
import br.uff.mvpcortes.prajuda.harvester.exception.InvalidRepositoryFormatException
import br.uff.mvpcortes.prajuda.harvester.exception.NonClonedRepositoryException
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
import java.io.IOException
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.diff.DiffEntry


@org.springframework.stereotype.Service
class GitHarvesterProcessor(val configService: ConfigService): HarvesterProcessor {

    companion object {
        val STR_GIT_HARVESTER_ID = "git_harvester"
    }

    private val logger: Logger = loggerFor(GitHarvesterProcessor::class)

    private class MyRefTag(val name:String, val ref:RevCommit)

    override fun harvest(service: PrajService, blockDeal: HarvestedConsumer) {

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
            val actualTagName = checkoutTag(repository, service).name

            //@see https://gist.github.com/steos/805992
            val actualTag = resolveTree(repository, actualTagName)

            val previousTagName = service.repositoryInfo.lastTag!!

            val previousTag = resolveTree(repository, previousTagName)
                        ?: throw IllegalStateException("Cannot found previous tag '${previousTagName}'")



            //https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowChangedFilesBetweenCommits.java
            //to 'object ... is not a tree'->https://paul.wellnerbou.de/2015/06/18/getting-commits-between-annotated-tags-with-jgit/
            val prajudaDir = getSafePrajudaDir(dirService, service)

            repository.newObjectReader().use{objectReader->
                val oldTreeIter = CanonicalTreeParser()
                oldTreeIter.reset(objectReader,previousTag)
                val newTreeIter = CanonicalTreeParser()
                newTreeIter.reset(objectReader, actualTag)

                Git(repository).use { git ->
                   getDiff(git, newTreeIter, oldTreeIter)
                        .asSequence()
                        .map { toPrajDocumentDiff(repository, actualTagName, service, it, prajudaDir)}
                        .forEach { blockDeal(it) }
                }

            }
        }
    }

    private fun resolveTree(repository: FileRepository, actualTagName: String) =
            repository.parseCommit(repository.resolve("refs/tags/${actualTagName}"))?.tree

    private fun getDiff(git: Git, newTreeIter: CanonicalTreeParser, oldTreeIter: CanonicalTreeParser): List<DiffEntry> {
        return git.diff()
                .setNewTree(newTreeIter)
                .setOldTree(oldTreeIter)
                .call()
    }

    private fun toPrajDocumentDiff(repository:Repository, tagName:String, service:PrajService, entry: DiffEntry, dirPrajuda:File):
            Harvested {
        val op : HarvesterProcessor.HarvestedOp = toOp(entry.changeType)
        return when (op) {
            HarvesterProcessor.HarvestedOp.NO_OP -> Harvested(op, PrajDocument())
            HarvesterProcessor.HarvestedOp.DELETED -> Harvested(op, createDeletedPrajDocument(service, entry))
            HarvesterProcessor.HarvestedOp.UPDATED -> {
                val documentFile=File(dirPrajuda, entry.newPath)
                return Harvested(op,
                        createPrajDocument(dirPrajuda, documentFile, tagName, repository.parseCommit(entry.newId.toObjectId()), service)
                )
            }
        }
    }

    private fun createDeletedPrajDocument(service:PrajService, entry: DiffEntry)=
            PrajDocument(
                    content = "",
                    tag = "",
                    path = entry.oldPath,//it.relativeTo(dirPrajuda).toString(),
                    serviceId = service.id,
                    serviceName = service.name
            )



    private fun toOp(changeType: DiffEntry.ChangeType)=
            when(changeType){
            /** Add a new file to the project  */
                DiffEntry.ChangeType.ADD -> HarvesterProcessor.HarvestedOp.UPDATED

            /** Modify an existing file in the project (content and/or mode)  */
                DiffEntry.ChangeType.MODIFY->HarvesterProcessor.HarvestedOp.UPDATED

            /** Delete an existing file from the project  */
                DiffEntry.ChangeType.DELETE->HarvesterProcessor.HarvestedOp.DELETED

            /** Rename an existing file to a new location  */
                DiffEntry.ChangeType.RENAME->HarvesterProcessor.HarvestedOp.UPDATED

            /** Copy an existing file to a new location, keeping the original  */
                DiffEntry.ChangeType.COPY->HarvesterProcessor.HarvestedOp.UPDATED

                else ->{
                    logger.warn("Cannot found op for $changeType. no_op selected")
                    HarvesterProcessor.HarvestedOp.NO_OP
                }
            }

//    private fun findPreviousTag(repository: FileRepository, previousTagName: String?)=


//       Git(repository).tagList().call()
//                .asSequence()
//                .filter{it.name == "refs/tags/$previousTagName"}
//                .map{Pair(getSafePeeledObjectId(repository, it), it.name.replace("refs/tags/", ""))}
//               .single()

    private fun getJGitTags(repository:Repository) = Git(repository).tagList().call().asSequence()


    /**
     * @see https://stackoverflow.com/a/12729335/8313595
     */
    private fun repositoryExists(dirService: File): Boolean {
        return try {
            FileRepository(File(dirService, ".git")).use{it.getObjectDatabase().exists()}
        } catch (e: IOException) {
            logger.warn("check repo exists fail: {}", e.message, e)
            false
        }
    }

    override fun harvestComplete(service: PrajService, blockDeal:HarvestedConsumer) {
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
                    .map{ createPrajDocument(dirPrajuda, it, revTag.name, revTag.ref, service) }
                    .map { Harvested(HarvesterProcessor.HarvestedOp.UPDATED, it) }
                    .forEach(blockDeal)

        }
    }

    private fun getSafePrajudaDir(dirService: File, service: PrajService): File {
        val dirPrajuda = File(dirService, service.documentDir)
        if (!dirPrajuda.exists() || !dirPrajuda.isDirectory) {
            throw InvalidRepositoryFormatException("Repository ${service.repositoryInfo.uri} does not have directory '${service.documentDir}'")
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


    private fun createPrajDocument(dirPrajuda:File, documentFile: File, tagName:String, revCommit:RevCommit, service: PrajService): PrajDocument {
        val tagDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(revCommit.commitTime.toLong()),
                revCommit.authorIdent.timeZone.toZoneId())

        return PrajDocument(
                content = documentFile.readText(),
                tag = tagName,
                path = documentFile.relativeTo(dirPrajuda).toString(),
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

        val tags =getJGitTags(repository)
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