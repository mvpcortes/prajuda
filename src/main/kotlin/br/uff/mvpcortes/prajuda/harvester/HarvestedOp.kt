package br.uff.mvpcortes.prajuda.harvester

import java.util.*

enum class HarvestedOp(val code:String){
    UPDATED("U"),
    DELETED("D"),
    NO_OP("_");

    companion object {
        fun random() = HarvestedOp.values()[Random().nextInt(HarvestedOp.values().size)]
    }
}