package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo

object PrajServiceFixture {

    const val DEFAULT_ID = "xxx.xxx.xxx.xxx"
    const val DEFAULT_NAME = "test"

    fun withName(name:String) = PrajService(name=name,
            harvesterTypeId = "git_classic",
            url = "https://$name.com/test",
            repositoryInfo = RepositoryInfo(uri="uri://$name.com"))

    fun withRepository(uri:String)= PrajService(name=DEFAULT_NAME, harvesterTypeId = "git_classic", repositoryInfo = RepositoryInfo(uri=uri), id=DEFAULT_ID)

    fun withRepositoryAndTag(uri: String, tag:String)= PrajService(name=DEFAULT_NAME, harvesterTypeId = "git_classic",  repositoryInfo = RepositoryInfo(uri=uri, lastTag=tag), id= DEFAULT_ID)

}