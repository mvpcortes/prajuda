package br.uff.mvpcortes.prajuda.service

import br.uff.mvpcortes.prajuda.harvester.FluxHarvesterProcessor
import br.uff.mvpcortes.prajuda.harvester.DefineHarvester
import br.uff.mvpcortes.prajuda.harvester.HarvesterType
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class HarvesterService(private val applicationContext:ApplicationContext) {

    val harvesterTypes=ArrayList<HarvesterType>(10)

    @PostConstruct
    fun init(){
        applicationContext
                .getBeansWithAnnotation(DefineHarvester::class.java)
                .asSequence()
                .map{   it.value }
                .map{ it as FluxHarvesterProcessor }.filter{it != null}
                .map{Pair(it, it::class.annotations.filter{ann->ann is DefineHarvester}.map{ ann->ann as DefineHarvester}.single())}
                .map{HarvesterType(getName(it.second), getId(it), it.first)}
                .forEach{harvesterTypes.add(it)}

    }

    private fun getId(ppp: Pair<FluxHarvesterProcessor, DefineHarvester>)=ppp.second.id.takeIf{!it.isBlank()}?:ppp.first::class.simpleName!!

    private fun getName(ann: DefineHarvester)=
            ann.name.takeIf { !it.isBlank() }
                    ?:ann.value.takeIf{!it.isBlank()}
                    ?:throw IllegalStateException("Cannot define um DefineHarvester with value/name empty")


}