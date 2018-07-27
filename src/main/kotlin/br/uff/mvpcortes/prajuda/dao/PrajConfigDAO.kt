package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.dao.interfaces.IExistsDAO
import br.uff.mvpcortes.prajuda.dao.interfaces.IGetDAO
import br.uff.mvpcortes.prajuda.model.PrajConfig

interface PrajConfigDAO: IGetDAO<PrajConfig>, IExistsDAO {

    companion object {
        const val DEFAULT_ID = "0"
    }

    fun get(): PrajConfig


    fun initConfigIfNotExists()

    fun deleteConfig()
}