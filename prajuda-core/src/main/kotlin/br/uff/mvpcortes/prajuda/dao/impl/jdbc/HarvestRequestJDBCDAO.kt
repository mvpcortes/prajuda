package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.HarvestRequestDAO
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.HarvestRequest
import br.uff.mvpcortes.prajuda.model.HarvestType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet
import java.time.LocalDateTime
import javax.annotation.PostConstruct

@Repository
class HarvestRequestJDBCDAO (final val jdbcTemplate: JdbcTemplate): HarvestRequestDAO{

    val logger = loggerFor(HarvestRequestJDBCDAO::class)

    private val simpleJdbcInsert: SimpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
            .withTableName(TABLE_NAME)
            .usingGeneratedKeyColumns(COLUMN_ID)


    companion object {
        const val TABLE_NAME = "harvest_request"
        const val COLUMN_ID = "id"
        const val COLUMN_SERVICE_SOURCE_ID = "service_source_id"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_STARTED_AT = "started_at"
        const val COLUMN_COMPLETED_AT = "completed_at"
        const val COLUMN_HARVEST_TYPE = "harvest_type"
        const val COLUMN_FAILED_EXCEPTION = "failed_exception"
        const val STR_SELECT_PROJECTION = """SELECT
                        $COLUMN_ID,
                        $COLUMN_SERVICE_SOURCE_ID,
                        $COLUMN_CREATED_AT,
                        $COLUMN_STARTED_AT,
                        $COLUMN_COMPLETED_AT,
                        $COLUMN_HARVEST_TYPE,
                        $COLUMN_FAILED_EXCEPTION
                    FROM $TABLE_NAME
                    """

        fun toArray(startedDate: LocalDateTime, ids: Collection<String>): Array<Any> {
            val newArray = Array<Any>(ids.size+1) {_->startedDate}
            ids.forEachIndexed { index, s ->  newArray[index+1] = s}
            return newArray
        }
    }


    @PostConstruct
    fun init() {
        simpleJdbcInsert.compile()
    }

    object defaultRowMapper:RowMapper<HarvestRequest>{
        override fun mapRow(rs: ResultSet, rowNum: Int): HarvestRequest {
            return HarvestRequest(id = rs.getString(1),
                    serviceSourceId = rs.getString(2),
                    createAt = rs.getTimestamp(3)!!.toLocalDateTime(),
                    startedAt = rs.getTimestamp(4)?.toLocalDateTime(),
                    completedAt = rs.getTimestamp(5)?.toLocalDateTime(),
                    harvestType = HarvestType.valueOf(rs.getString(6)!!),
                    failed = rs.getString(7))
        }

    }

    @Transactional
    override fun completeRequests(ids: Collection<String>):Int {
        return if(ids.isEmpty()){
            0
        }else {
            jdbcTemplate.update(
                    """
                    UPDATE $TABLE_NAME SET $COLUMN_COMPLETED_AT = ? WHERE id IN (${ids.asSequence().map { '?' }.joinToString()})
                """.trimIndent(),
                    *toArray(LocalDateTime.now(), ids))
        }
    }

    override fun getOldOpen(qtd:Int): List<HarvestRequest> =  jdbcTemplate.query(
            """$STR_SELECT_PROJECTION
                    WHERE $COLUMN_CREATED_AT IS NOT NULL
                    AND $COLUMN_STARTED_AT IS NULL
                    LIMIT ? OFFSET 0
                """, arrayOf(qtd), defaultRowMapper)


    /**
     * We should create a new transaction every time because we cannot contaminate start operation
     */
    @Transactional()
    override fun getAndStartOldOpen(qtd:Int): List<HarvestRequest> =
            when {
                qtd < 0 -> throw IllegalArgumentException("Cannot get negative qtd")
                qtd == 0 -> {
                    logger.warn("deal with getAndStart zero elements")
                    emptyList()
                }
                else -> getOldOpen(qtd)
                        .map { it.toStarted() }
                        .let { list -> startRequests(LocalDateTime.now(), list.map { it.id!! }); list }
            }

    override fun startRequests(startedDate: LocalDateTime,  ids:List<String>):Int {
        logger.info("{}, {}", startedDate, ids)
        return if(ids.isEmpty()){
            0
        }else {
            jdbcTemplate.update(
                    """
                    UPDATE $TABLE_NAME SET $COLUMN_STARTED_AT = ? WHERE id IN (${ids.asSequence().map { '?' }.joinToString()})
                """.trimIndent(),
                    *toArray(startedDate, ids))
        }
    }

    @Transactional()
    override fun completeRequest(request: HarvestRequest):Int
            = jdbcTemplate.update(
            """
                UPDATE $TABLE_NAME SET $COLUMN_COMPLETED_AT = ? WHERE id = ?
            """.trimIndent(),
                arrayOf(LocalDateTime.now(), request.id)
    )


    @Transactional
    override fun failRequest(id:String, tw:Throwable):Int =
        jdbcTemplate.update(
                "UPDATE $TABLE_NAME SET $COLUMN_COMPLETED_AT = ?, $COLUMN_FAILED_EXCEPTION = ? WHERE id = ?",
                LocalDateTime.now(), HarvestRequest.toStringException(tw), id)


    override fun findById(id: String)
        = jdbcTemplate.queryForObjectNullable(
                "$STR_SELECT_PROJECTION WHERE $COLUMN_ID = ?",
            arrayOf<Any>(id), defaultRowMapper)



    override fun save(request: HarvestRequest): HarvestRequest {
        if (request.id == null || request.id?.isBlank() == true) {
            request.id = simpleJdbcInsert.executeAndReturnKey(createParameterSource(request)).toString()
        } else {
            jdbcTemplate.update(
                    """
                    UPDATE $TABLE_NAME SET
                        $COLUMN_COMPLETED_AT = ?,
                        $COLUMN_SERVICE_SOURCE_ID = ?,
                        $COLUMN_CREATED_AT = ?,
                        $COLUMN_STARTED_AT = ?,
                        $COLUMN_COMPLETED_AT = ?,
                        $COLUMN_HARVEST_TYPE = ?
                    WHERE
                        $COLUMN_ID = ?
                """.trimIndent(),
                    request.completedAt,
                    request.serviceSourceId,
                    request.createAt,
                    request.startedAt,
                    request.completedAt,
                    request.harvestType.name,
                    request.id)
        }
        return request
    }

    override fun deleteAll(): Int = jdbcTemplate.update("DELETE FROM $TABLE_NAME")


    private fun createParameterSource(hr: HarvestRequest) = MapSqlParameterSource(mapOf(
            COLUMN_STARTED_AT to hr.startedAt,
            COLUMN_COMPLETED_AT to hr.completedAt,
            COLUMN_CREATED_AT to hr.createAt,
            COLUMN_SERVICE_SOURCE_ID to hr.serviceSourceId,
            COLUMN_HARVEST_TYPE to hr.harvestType.name
    ))

}