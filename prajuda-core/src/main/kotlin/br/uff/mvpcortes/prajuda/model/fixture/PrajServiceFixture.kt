package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo

object PrajServiceFixture {

    const val DEFAULT_ID = "xxx.xxx.xxx.xxx"
    const val DEFAULT_NAME = "test"

    fun withDocumentDir(
            documentDir:String=PrajService.STR_AJUDA_DIR,
            name:String=DEFAULT_NAME) = withName(name=name, documentDir=documentDir)



    fun withName(name:String, documentDir:String=PrajService.STR_AJUDA_DIR) = PrajService(name=name,
            harvesterTypeId = "git_classic",
            url = "https://$name.com/test",
            documentDir = documentDir,
            repositoryInfo = RepositoryInfo(uri="uri://$name.com"))

    fun withRepository(uri:String)=withRepositoryAndTag(uri, tag=null)

    fun withRepositoryAndTag(uri: String, tag:String?)=
            PrajService(name=DEFAULT_NAME, harvesterTypeId = "git_classic", repositoryInfo = RepositoryInfo(uri=uri, lastTag=tag), id= DEFAULT_ID)

}