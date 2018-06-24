package br.uff.mvpcortes.prajuda.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName ="prajuda.admin", type="config")
class PrajConfig(@Id var id:String?=null, val name:String ="prajuda")
