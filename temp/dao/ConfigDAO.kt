package br.uff.mvpcortes.prajuda.daoprototype.dao

import br.uff.mvpcortes.prajuda.model.Config
import org.springframework.data.repository.CrudRepository

//import org.springframework.data.repository.CrudRepository

interface ConfigDAO: CrudRepository<Config, String> {

}
//interface ConfigDAO : CrudRepository<PrajCondig, String> {
//
//}