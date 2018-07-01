package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo

object PrajServiceFixture {

    val DEFAULT_ID = "xxx.xxx.xxx.xxx"
    val DEFAULT_NAME = "test"

    fun withName(name:String) = PrajService(name=name, repositoryInfo = RepositoryInfo(uri="uri://$name"))

    fun withRepository(uri:String)= PrajService(name=DEFAULT_NAME, repositoryInfo = RepositoryInfo(uri=uri), id=DEFAULT_ID)

    fun withRepositoryAndTag(uri: String, tag:String)= PrajService(name=DEFAULT_NAME, repositoryInfo = RepositoryInfo(uri=uri, lastTag=tag), id= DEFAULT_ID)

}