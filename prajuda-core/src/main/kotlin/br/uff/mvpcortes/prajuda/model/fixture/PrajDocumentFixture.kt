package br.uff.mvpcortes.prajuda.model.fixture

import br.uff.mvpcortes.prajuda.model.PrajDocument
import br.uff.mvpcortes.prajuda.model.PrajService

object PrajDocumentFixture {


    const val STR_MD_SIMPLE = "# Title\n## Subtitle\nparagraph"
    const val STR_VALID_MD_SIMPLE = "<h1>Title</h1>\n<h2>Subtitle</h2>\n<p>paragraph</p>\n"

    fun default(id:String?="1", tag:String="tag",
                path:String="test/main.md", serviceId:String="1",
                serviceName:String="my-service",
                content:String=STR_MD_SIMPLE) = PrajDocument(
            id=id,
            content=content,
            tag=tag,
            path=path,
            serviceId=serviceId,
            serviceName=serviceName
    )

    fun new()= default(id=null)

    fun new(path:String="test/main",
            content:String=STR_MD_SIMPLE,
            prajService: PrajService)=
            default(id=null,
                    path=path,
                    content=content,
                    serviceId = prajService.id!!,
                    serviceName = prajService.name)
}