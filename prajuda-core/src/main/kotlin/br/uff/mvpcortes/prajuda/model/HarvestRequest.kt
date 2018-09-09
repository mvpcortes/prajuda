package br.uff.mvpcortes.prajuda.model
import java.io.PrintWriter
import java.io.StringWriter
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
        val completedAt:LocalDateTime?=null,
        val failed:String? = null
        ):WithId{

    companion object {

        fun toStringException(exception: Throwable): String {
            val strWriter = StringWriter()
            val writer = PrintWriter(strWriter)
            writer.println("${exception::class}:${exception.message}")
            exception.printStackTrace(writer)
            return strWriter.toString().take(65_000)
        }

    }

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
        throw IllegalStateException("Cannot to complete a not started harvester request ($harvesterStatus)")

    fun toFailed(exception:Throwable) = if(harvesterStatus == HarvesterStatus.PROCESSING)
            this.copy(completedAt = java.time.LocalDateTime.now(), failed = toStringException(exception))
        else
            throw IllegalStateException("Cannot to fail a not started harvester request ($harvesterStatus)")



}


enum class HarvesterStatus{
    OPEN,
    PROCESSING,
    COMPLETE
}