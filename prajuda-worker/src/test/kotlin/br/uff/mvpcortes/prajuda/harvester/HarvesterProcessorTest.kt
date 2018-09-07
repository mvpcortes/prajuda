package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class HarvesterProcessorTest{

    object sut:HarvesterProcessor{
        override fun harvestComplete(service: PrajService, blockDeal: (Harvested)->Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun harvest(service: PrajService, blockDeal: (Harvested)->Unit) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    @ParameterizedTest(name = "run #{index} with tag [{0}]->[{1}]")
    @CsvSource(value= ["prajuda/teste.md, true", "xuxu/xaxa.md, true", "prajuda/.gitkeep, false", "prajuda/manual.markdown, true", "big/ultra/greate/namePath/to/file.md, true", "root/p.md, true", "lolou.md, true", "lolou.m, false", "lolou.txt, true", ".md, false", "diary.txt, true"])
    fun `verify path is markdown`(path:String, result:Boolean){
        assertThat(sut.acceptPath(path)).isEqualTo(result)
    }

    @ParameterizedTest(name = "run #{index} with file name {0} should be equal to {1}")
    @CsvSource(value= ["xuxu.md, xuxu", "xaxa.md, xaxa", "file.exe, file", "my_name_is_jonny.my_name_is_jonny ,my_name_is_jonny",
        "big/ultra/greate/namePath/to/file.md, big/ultra/greate/namePath/to/file",
        "i.md/have.m/a.a/lot.t/of.of/names.md, i.md/have.m/a.a/lot.t/of.of/names"])
    fun `when call removeExtension then then remove extension`(path:String, result:String){
        assertThat(sut.removeExtension(path)).isEqualTo(result)
    }
}