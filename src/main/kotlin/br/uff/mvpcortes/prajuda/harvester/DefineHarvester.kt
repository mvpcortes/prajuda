package br.uff.mvpcortes.prajuda.harvester

import org.springframework.core.annotation.AliasFor

annotation class DefineHarvester(
        @get:AliasFor(annotation = DefineHarvester::class, attribute = "name")val value:String="",
        val name:String="",
        val id:String="") {
}