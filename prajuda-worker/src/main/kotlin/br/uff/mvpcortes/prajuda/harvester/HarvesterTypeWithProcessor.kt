package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.HarvesterType

class HarvesterTypeWithProcessor(_name:String, _id:String, val processor: FluxHarvesterProcessor):
        HarvesterType(name=_name, id=_id){
    override fun toString(): String {
        return "HarvesterTypeWithProcessor(name='$name', id='$id', processor=$processor)"
    }
}