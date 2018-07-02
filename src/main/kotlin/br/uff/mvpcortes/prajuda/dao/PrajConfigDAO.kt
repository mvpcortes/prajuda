package br.uff.mvpcortes.prajuda.dao

import br.uff.mvpcortes.prajuda.model.PrajConfig

interface PrajConfigDAO{

    companion object {
        const val DEFAULT_ID = "0"
    }

    fun get():PrajConfig

    fun initConfigIfNotExists()

    fun exists(defaultId: String): Boolean
    fun deleteConfig()
}