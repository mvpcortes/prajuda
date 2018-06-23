package br.uff.mvpcortes.prajuda.model

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import java.time.LocalDateTime

@Document(indexName ="prajuda.data", type="document")
class PrajDocument (
        @Id val id:String?=null,
                    var content:String="",
                    var tag:String="",
                    /**
                     * path to file in repository
                     */
                    var path:String="",

                    /**
                     * A document can  have not service (orphan?)
                     */
                    var serviceId:String?=null,
                    /**
                     * The instant that this page was harvest
                     */
                    var harvestDate:LocalDateTime=LocalDateTime.now(),

                    /**
                     * The instant that this page was modified in original repository
                     */
                    var tagDate:LocalDateTime =LocalDateTime.now()){}