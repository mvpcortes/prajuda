package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.harvester.Harvested
import br.uff.mvpcortes.prajuda.harvester.HarvestedOp
import br.uff.mvpcortes.prajuda.model.PrajDocument

object HarvestedFixture {

    fun update(name:String, i:Int)= create(HarvestedOp.UPDATED, name, i)
    fun delete(name:String, i:Int)= create(HarvestedOp.DELETED, name, i)
    fun noop  (name:String, i:Int)= create(HarvestedOp.NO_OP  , name, i)
/*    fun random(name:String, i:Int) = create(HarvestedOp.values()[Random().nextInt(3)], name, i)*/

    fun create(op:HarvestedOp, name:String, i:Int) =Harvested(op, PrajDocument(path="${op.code}_${name}_$i.md"))
}