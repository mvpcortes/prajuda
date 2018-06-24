package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.RepositoryInfo
import br.uff.mvpcortes.prajuda.modelService.PrajService
import java.util.*

object PrajServiceFixture {

    fun withRepository(uri:String)= PrajService(name="teste", repositoryInfo = RepositoryInfo(uri=uri), id= "xxx.xxx.xxx.xxx")

}