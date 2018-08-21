package br.uff.mvpcortes.prajuda.harvester

import kotlin.reflect.KClass

class HarvesterType(val name:String, val id:String, val processor: FluxHarvesterProcessor){
    override fun toString(): String {
        return "HarvesterType(name='$name', id='$id', processor=$processor)"
    }
}