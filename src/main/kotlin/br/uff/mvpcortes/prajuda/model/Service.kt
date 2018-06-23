package br.uff.mvpcortes.prajuda.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.time.LocalDateTime
import java.util.*

@Document(indexName ="prajuda.admin", type="service")
data class Service(
        @Id var id:String?=null,
        val name:String = "",
        val url:String = "n/a",
        val harvesterTypeId:String="",
        val repositoryInfo:RepositoryInfo=RepositoryInfo(),
        val workDir:String = UUID.randomUUID().toString().replace("-","")
){}


data class RepositoryInfo(val url:String="",
                          val branch:String="master",
                          val lastModified:LocalDateTime =LocalDateTime.MIN,
                          val lastTag:String?=null,
                          val username:String="",
                          val password:String=""){}