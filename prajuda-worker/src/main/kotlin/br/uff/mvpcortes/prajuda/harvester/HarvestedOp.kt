package br.uff.mvpcortes.prajuda.harvester

import java.util.*

enum class HarvestedOp(val code:String){
    UPDATED("U"),
    DELETED("D"),
    FAIL("F"),//when fail to get repository emit a Harvested with this op. It will used to flag request with error
    NO_OP("_");

    companion object {
        fun random() = HarvestedOp.values()[Random().nextInt(HarvestedOp.values().size)]
    }
}