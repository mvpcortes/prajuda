package br.uff.mvpcortes.prajuda.harvester

import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.fixture.PrajDocumentFixture

object HarvestedFixture {

    fun update(name:String="xuxu", id:Int?, serviceId:String="10", tag:String="1", path:String="n_a")= create(HarvestedOp.UPDATED, name, id, serviceId, tag)
    fun delete(name:String="xuxu", id:Int?, serviceId:String="10", tag:String="1", path:String="n_a")= create(HarvestedOp.DELETED, name, id, serviceId, tag)
    fun noop  (name:String="xuxu", id:Int?, serviceId:String="10", tag:String="1", path:String="n_a")= create(HarvestedOp.NO_OP  , name, id, serviceId, tag)
/*    fun random(name:String, i:Int) = createHelper(HarvestedOp.values()[Random().nextInt(3)], name, i)*/

    fun create(op:HarvestedOp, name:String="xuxu", id:Int?, serviceId:String="10", tag:String="1") =
            Harvested(op, PrajDocument(id=id?.toString(), path="${op.code}_${name}_$id.md", serviceId=serviceId, tag = tag))

}