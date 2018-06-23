package br.uff.mvpcortes.prajuda.harvester.git
//
//import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
//import br.uff.mvpcortes.prajuda.harvester.HarvesterProcessor
//import br.uff.mvpcortes.prajuda.loggerFor
//import br.uff.mvpcortes.prajuda.model.PrajDocument
//import br.uff.mvpcortes.prajuda.model.Service
//import br.uff.mvpcortes.prajuda.service.config.ConfigService
//import org.eclipse.jgit.api.Git
//import org.eclipse.jgit.diff.DiffEntry
//import org.eclipse.jgit.internal.storage.file.FileRepository
//import org.eclipse.jgit.lib.ObjectReader
//import org.eclipse.jgit.lib.PersonIdent
//import org.eclipse.jgit.lib.Repository
//import org.eclipse.jgit.revwalk.RevWalk
//import org.eclipse.jgit.storage.file.FileRepositoryBuilder
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
//import org.eclipse.jgit.treewalk.CanonicalTreeParser
//import org.slf4j.Logger
//import reactor.core.publisher.Flux
//import java.io.File
//import java.time.LocalDateTime
//
//class GitProcessor(val service:Service,
//                   val configService: ConfigService,
//                   val logger: Logger = loggerFor(GitProcessor::class.java),
//                   val prajDocumentDAO: PrajDocumentDAO): HarvesterProcessor {
//    override fun harvest(): Flux<PrajDocument> {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun harvest( blockDeal: (HarvesterProcessor.Harvested) -> Unit) {
//        val isComplete = tryInitLocalRepository();
//
//        if(isComplete){
//            internalHarvestComplete(blockDeal);
//        }else{
//            internalHarvest( blockDeal)
//        }
//    }
//
//
//    private fun getTreeIter(objReader:ObjectReader, repo:Repository, tag:String): CanonicalTreeParser{
//        val oid = repo.resolve(tag);
//        val treeIter = CanonicalTreeParser()
//        treeIter.reset(objReader, oid)
//        return treeIter
//    }
//
//    private fun toHarvested(diffEntry: DiffEntry, author:PersonIdent):HarvesterProcessor.Harvested=
//            HarvesterProcessor.Harvested(op= getOp(diffEntry), doc=createDocument(diffEntry, author))
//
//    private fun createDocument(diffEntry: DiffEntry,  author:PersonIdent): PrajDocument {
//        return fillDocument(
//                diffEntry.oldPath
//                        .let(prajDocumentDAO::findByPath)
//                        ?:PrajDocument(),
//                diffEntry,
//                author)
//    }
//
//    private fun fillDocument(document: PrajDocument, diffEntry: DiffEntry, author: PersonIdent):PrajDocument {
//        document.path = diffEntry.newPath
//        document.serviceId = this.service.id!!
//        document.tag = diffEntry.newId!!.name()
//        document.harvestDate = LocalDateTime.now()
//        document.tagDate = LocalDateTime.ofInstant(author.`when`.toInstant(), author.timeZone!!.toZoneId())
//        document.content = getContent(diffEntry)
//        return document
//    }
//
//    private fun getContent(diffEntry: DiffEntry): String {
//        val path = diffEntry.newPath
//        val file = File(this.getLocalWorkDir(), path);
//        return file.readText()
//    }
//
//    private fun getOp(diffEntry: DiffEntry): HarvesterProcessor.HarvestedOp {
//        return when(diffEntry.changeType){
//            DiffEntry.ChangeType.ADD    -> HarvesterProcessor.HarvestedOp.UPDATED
//            DiffEntry.ChangeType.RENAME -> HarvesterProcessor.HarvestedOp.UPDATED
//            DiffEntry.ChangeType.COPY   -> HarvesterProcessor.HarvestedOp.UPDATED
//            DiffEntry.ChangeType.MODIFY -> HarvesterProcessor.HarvestedOp.UPDATED
//            DiffEntry.ChangeType.DELETE -> HarvesterProcessor.HarvestedOp.DELETED
//        }
//    }
//
//    /**
//     * @see |https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ShowChangedFilesBetweenCommits.java
//     */
//    private fun internalHarvest( blockDeal: (HarvesterProcessor.Harvested) -> Unit) {
//        val previousTagName = this.service.repositoryInfo.lastTag!!
//        createRepository(checkoutBranch(openOrCloneGitRepository()))
//                .use {repository->
//
//                    val actualTagName = Git(repository).use { it.describe().call() }
//                    val authorEntry = getAuthorIdent(repository, actualTagName)
//
//                    logger.info("get diff between {} and {}", actualTagName, previousTagName);
//
//                    repository.newObjectReader().use { objReader ->
//
//                        Git(repository).use { git ->
//                            git.diff()
//                                    .setNewTree(getTreeIter(objReader, git.repository, actualTagName))
//                                    .setOldTree(getTreeIter(objReader, git.repository, previousTagName))
//                                    .call()
//                                    .asSequence()
//                                    .map{diffEntry->toHarvested(diffEntry, authorEntry)}
//                                    .forEach(blockDeal)
//                        }
//                    }
//                }
//
//    }
//
//    private fun getAuthorIdent(repo:Repository, actualTagName: String?): PersonIdent {
//        return RevWalk(repo).use{it.parseCommit(repo.resolve(actualTagName)).authorIdent }
//
//    }
//
//    private fun getLocalWorkDir():File{
//        return File(configService.getWorkDirectoryForHarvester(getIdHarvester()), service.name)
//                .takeIf { (it.exists() && it.isDirectory()) || it.mkdir() }
//                ?:throw IllegalStateException("Cannot create workdir for  service ${service.name}")
//    }
//
//    private fun createRepository(git:Git)= FileRepositoryBuilder()
//                .setMustExist( true )
//                .setGitDir(         git.use{it.repository.workTree} )
//                .build()
//
//    private fun checkoutBranch(git:Git):Git{
//        //checkout branch and pull
//        git.checkout().setName(service.repositoryInfo.branch).call()//checkout the branch
//        git.pull().setCredentialsProvider(createntialProvider()).call()//pull the branch
//        return git
//    }
//
//    /**
//     * I do not put this method in Service.RepositoryInfo because the RepositoryInfo should be agnostic about the Credentials. In the future it should not have specific credentials
//     */
//    private fun createntialProvider()
//            = UsernamePasswordCredentialsProvider(service.repositoryInfo.username, service.repositoryInfo.password)
//
//
//    private fun openOrCloneGitRepository(): Git {
//        val fileWorkDir=getLocalWorkDir()
//        return if (!isValidRepository(fileWorkDir)) {
//            fileWorkDir.deleteRecursively()
//            fileWorkDir.mkdir()
//            cloneRepository(fileWorkDir)
//        } else {
//            Git.open(fileWorkDir)
//        }
//    }
//
//    private fun cloneRepository(fileServiceDir: File)= Git.cloneRepository()
//            .setDirectory(fileServiceDir)
//            .setRemote(service.repositoryInfo.url)
//            .call()
//
//
//    /**
//     * Verify there is a git repository on directory
//     */
//    fun isValidRepository(file: File):Boolean  = try { FileRepository(file).objectDatabase.exists() }catch(e:Exception){ false }
//
//    override fun harvestComplete(service: Service, blockDeal: (HarvesterProcessor.Harvested) -> Unit) {
//        tryInitLocalRepository()
//        internalHarvestComplete(service, blockDeal)
//    }
//
//    override fun getIdHarvester(): String {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//
//    private fun tryInitLocalRepository(): Boolean {
//
//    }
//
//
//    private fun internalHarvestComplete( blockDeal: (HarvesterProcessor.Harvested) -> Unit) {
//
//    }
//
//}