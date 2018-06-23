//package br.uff.mvpcortes.prajuda.harvester.git
//
//import br.uff.mvpcortes.prajuda.harvester.HarvesterProcessor
//import br.uff.mvpcortes.prajuda.model.Service
//import br.uff.mvpcortes.prajuda.service.config.ConfigService
//import org.eclipse.jgit.api.Git
//import org.eclipse.jgit.internal.storage.file.FileRepository
//import org.eclipse.jgit.lib.FileMode
//import org.eclipse.jgit.lib.ObjectLoader
//import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
//import org.eclipse.jgit.treewalk.TreeWalk
//import org.slf4j.LoggerFactory
//import java.io.File
//
//
//@org.springframework.stereotype.Service
//class GitProcessorOld(val configService: ConfigService): HarvesterProcessor{
//
//    val logger=LoggerFactory.getLogger(GitProcessorOld::class.java)
//
//    override fun getIdHarvester() =  javaClass.simpleName
//
//
////    override fun harvest(mode: HarvesterProcessor.HarvesterMode, service: Service): Flux<PrajDocument>{
////
////        return Flux.create<PrajDocument>{ sink:FluxSink<PrajDocument>->
//////            sink.next(null);
////       val dirWork = cloneAndCheckoutRepository(service)
////
////        dirWork?.walkTopDown()
////                ?.asSequence()
////                ?.filter { it.isFile }
////                ?.map { getChangedDate(it)}
////                ?.map{ toPrajDocument(it)}
////                ?.forEach {
////                    val pd:PrajDocument=PrajDocument()
////                    sink.next(pd)
////                }
////
////        sink.complete()
////        }
////    }
//
//    override fun harvest(service: Service, blockDeal:(HarvesterProcessor.Harvested)->Unit){
//        tryInitRepository(service);
//    }
//
//    override fun harvestComplete(service:Service,blockDeal:(HarvesterProcessor.Harvested)->Unit){
//
//    }
//
//    /**
//     * @see https://stackoverflow.com/questions/26917239/file-commit-date-in-jgit
//     */
//    private fun getChangedDate(it: File): ChangedInfo {
//
//    }
//
//    data class ChangedInfo (val lastCommit:String, val lastHarvestCommit:String){
//
//        fun isChanged() = lastCommit != lastHarvestCommit
//    }
//
//    private fun toPrajDocument(it: File): Any {
//val fileMode:                FileMode? = null
//        val o: ObjectLoader?=null;
//        val x: TreeWalk? = null
//        o.
//    }
//
//    fun cloneAndCheckoutRepository(service: Service):File? {
//        val fileServiceDir = File(configService.getWorkDirectoryForHarvester(getIdHarvester()), service.name)
//                .takeIf { (it.exists() && it.isDirectory()) || it.mkdir() }
//                ?:throw IllegalStateException("Cannot create workdir for  service ${service.name}")
//
//        return openOrCloneGitRepository(fileServiceDir, service).use {
//            try {
//                checkoutPullbranch(it, service)
//                checkoutTag(it)
//            }catch(e:Exception) {
//                throw IllegalStateException("Cannot checkout last tag of branch ${service.repositoryInfo.branch}", e);
//            }
//
//            val filePrajuda = File(fileServiceDir, service.workDir)
//
//            if(filePrajuda.exists() && filePrajuda.isDirectory()) {
//                return filePrajuda
//            }else {
//                logger.info("There is not prajuda dir on repository: {}", service.workDir)
//                return null
//            }
//        }
//    }
//
//    private fun checkoutTag(it: Git) {
//        //checkout tag
//        val tagName = it.describe().call()
//        it.checkout().setName(tagName).call()
//    }
//
//    private fun checkoutPullbranch(it: Git, service: Service) {
//        //checkout branch and pull
//        it.checkout().setName(service.repositoryInfo.branch).call()//checkout the branch
//        it.pull().setCredentialsProvider(createntialProvider(service)).call()
//    }
//
//    /**
//     * I do not put this method in Service.RepositoryInfo because the RepositoryInfo should be agnostic about the Credentials. In the future it should not have specific credentials
//     */
//    private fun createntialProvider(service: Service) = UsernamePasswordCredentialsProvider(service.repositoryInfo.username, service.repositoryInfo.password)
//
//    private fun openOrCloneGitRepository(fileServiceDir: File, service: Service): Git {
//        return if (!isValidRepository(fileServiceDir)) {
//            fileServiceDir.deleteRecursively()
//            fileServiceDir.mkdir()
//            cloneRepository(service, fileServiceDir)
//        } else {
//            Git.open(fileServiceDir)
//        }
//    }
//
//    private fun cloneRepository(service: Service, fileServiceDir: File)= Git.cloneRepository()
//                .setDirectory(fileServiceDir)
//                .setRemote(service.repositoryInfo.url)
//                .call()
//
//
//    /**
//     * Verify there is a git repository on directory
//     */
//    fun isValidRepository(file:File):Boolean  = try { FileRepository(file).objectDatabase.exists() }catch(e:Exception){ false }
//}
