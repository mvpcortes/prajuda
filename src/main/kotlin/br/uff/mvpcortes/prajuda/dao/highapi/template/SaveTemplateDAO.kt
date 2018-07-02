package br.uff.mvpcortes.prajuda.dao.highapi.template

interface SaveTemplateDAO<T> {

    fun save(t:T)
}