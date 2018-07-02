package br.uff.mvpcortes.prajuda.dao.highapi.template

interface GetTemplateDAO <T> {
    fun get(id:String):T
}