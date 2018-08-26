package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.PrajDocument

object PrajDocumentFixture {

    fun default(id:String?="1", tag:String="tag", path:String="test/main.md", serviceId:String="1", serviceName:String="my-service") = PrajDocument(
            id=id,
            content="XUXU",
            tag=tag,
            path=path,
            serviceId=serviceId,
            serviceName=serviceName
    )

    fun new()= default(id=null)


}