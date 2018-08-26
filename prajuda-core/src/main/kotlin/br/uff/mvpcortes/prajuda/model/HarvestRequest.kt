package br.uff.mvpcortes.prajuda.model
import java.time.LocalDateTime


/**
 * Represents a request to process a harvester on one repository
 */
data class HarvestRequest(

        override var id:String?=null,
        /**
         * the service serviceSourceId of harvester
         */
        val serviceSourceId:String = "",
        /**
         * type of harvest
         */
        val harvestType:HarvestType = HarvestType.COMPLETE,
        /**
         * Date creation of request
         */
        val createAt:LocalDateTime = LocalDateTime.now(),
        /**
         * when harvester started
         */
        val startedAt:LocalDateTime?=null,
        /**
         * when harvester completed
         */
        val completedAt:LocalDateTime?=null):WithId{


        /**
         * Status havester
         */
        val harvesterStatus: HarvesterStatus
            get()= if(startedAt != null){
                if(completedAt != null) {
                    HarvesterStatus.COMPLETE
                }else{
                    HarvesterStatus.PROCESSING
                }
            }else {
                HarvesterStatus.OPEN
            }

    fun toStarted()= if(harvesterStatus == HarvesterStatus.OPEN)
        this.copy( startedAt = java.time.LocalDateTime.now())
    else
        throw IllegalStateException("Cannot start a not opening harvester request ($harvesterStatus)")

    fun toCompleted()= if(harvesterStatus == HarvesterStatus.PROCESSING)
        this.copy( completedAt = java.time.LocalDateTime.now())
    else
        throw IllegalStateException("Cannot complete a not started harvester request ($harvesterStatus)")
}


enum class HarvesterStatus{
    OPEN,
    PROCESSING,
    COMPLETE
}