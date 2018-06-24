package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo

object PrajServiceFixture {

    fun withRepository(uri:String)= PrajService(name="teste", repositoryInfo = RepositoryInfo(uri=uri), id= "xxx.xxx.xxx.xxx")

}