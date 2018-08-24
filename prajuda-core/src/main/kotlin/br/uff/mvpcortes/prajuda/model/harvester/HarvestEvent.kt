import java.time.LocalDateTime

class HarvestEvent(
        val source:String,
        val createAt:LocalDateTime = LocalDateTime.now(),
        val initiedAt:LocalDateTime?)