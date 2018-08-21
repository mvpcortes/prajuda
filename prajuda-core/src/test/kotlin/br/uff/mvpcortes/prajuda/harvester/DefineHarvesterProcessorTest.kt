package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DefineHarvesterProcessorTest{

    object sut:HarvesterProcessor{
        override fun harvestComplete(service: PrajService, blockDeal: (Harvested)->Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun harvest(service: PrajService, blockDeal: (Harvested)->Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    @ParameterizedTest(name = "run #{index} with tag [{0}]->[{1}]")
    @CsvSource(value= ["prajuda/teste.md, true", "xuxu/xaxa.md, true", "prajuda/.gitkeep, false", "prajuda/manual.markdown, true", "big/ultra/greate/path/to/file.md, true", "root/p.md, true", "lolou.md, true", "lolou.m, false", "lolou.txt, true", ".md, false", "diary.txt, true"])

    fun `verify path is markdown`(path:String, result:Boolean){
        assertThat(sut.acceptPath(path)).isEqualTo(result)
    }
}