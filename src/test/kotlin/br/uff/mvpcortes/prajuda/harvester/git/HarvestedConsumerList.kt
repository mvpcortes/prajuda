package br.uff.mvpcortes.prajuda.harvester.git

import br.uff.mvpcortes.prajuda.GitTestRepository
import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import java.util.function.Consumer

class HarvestedConsumerList: ArrayList<Harvested>(10) {

    fun consumer():(Harvested)->Unit = this::addU

    private inner class ConsumerImpl:Consumer<Harvested>{
        override fun accept(p0: Harvested) =  addU(p0)
    }

    fun javaConsumer(): Consumer<Harvested> = ConsumerImpl()


    fun addU(h:Harvested){
        this.add(h)
    }

    fun sort()= this.sortWith (compareBy({it.op}, {it.doc.path}) )



    fun assertFourthCommit() {
        sort()

        assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "4")
        assertHarvestedUpdated(1, "src/code.md", GitTestRepository.STR_CODE_MD, "4")
        assertHarvestedUpdated(2, "src/user.md", "class user test content", "4")
        Assertions.assertThat(this).hasSize(3)
    }

    fun assertDiffSecondToFouthCommit(){
        sort()

        assertHarvestedUpdated(0, "org/main.md", "xuxu xaxa", "4")
        assertHarvestedUpdated(1, "src/user.md", "class user test content", "4")
        assertHarvestedDeleted(2, "main.md")
        Assertions.assertThat(this).hasSize(3)
    }

    fun assertHarvestedDeleted(id: Int, path: String) {
        assertThat(this[id].op).isEqualTo(HarvestedOp.DELETED)
        assertThat(this[id].doc.id).isNull()
        assertThat(this[id].doc.content).isBlank()
        assertThat(this[id].doc.path).isEqualTo(path)
        assertThat(this[id].doc.tag).isEmpty()
        assertThat(this[id].doc.serviceId).isEqualTo(PrajServiceFixture.DEFAULT_ID)
        assertThat(this[id].doc.serviceName).isNull()//we do not need a name here
    }

    fun assertHarvestedUpdated(id: Int, path: String, content: String, tag: String) {
        assertThat(this[id].op).isEqualTo(HarvestedOp.UPDATED)
        assertThat(this[id].doc.id).isNull()
        assertThat(this[id].doc.content).isEqualTo(content)
        assertThat(this[id].doc.path).isEqualTo(path)
        assertThat(this[id].doc.tag).isEqualTo(tag)
        assertThat(this[id].doc.serviceId).isEqualTo(PrajServiceFixture.DEFAULT_ID)
        assertThat(this[id].doc.serviceName).isEqualTo(PrajServiceFixture.DEFAULT_NAME)
    }

}