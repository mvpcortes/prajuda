package br.uff.mvpcortes.prajuda.model

import java.time.LocalDateTime

data class RepositoryInfo(val uri:String="",
                          val branch:String="master",
                          val lastModified: LocalDateTime = LocalDateTime.MIN,
                          val lastTag:String?=null,
                          val username:String="",
                          val password:String=""){}