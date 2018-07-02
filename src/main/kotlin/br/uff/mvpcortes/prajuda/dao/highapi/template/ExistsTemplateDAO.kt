package br.uff.mvpcortes.prajuda.dao.highapi.template

interface ExistsTemplateDAO {

    fun exists(id: String): Boolean

    fun <T> notExists(id:String, func:(id:String)->T): T? = if(!exists(id)) func(id) else null
}