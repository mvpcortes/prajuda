package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.loggerFor
import br.uff.mvpcortes.prajuda.model.PrajDocument
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.ResultSet


@Repository
class PrajDocumentJDBCDAO(final val jdbcTemplate:JdbcTemplate):PrajDocumentDAO {

    companion object {
        const val TABLE_NAME = "praj_document"
        const val COLUMN_ID = "id"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TAG = "tag"
        const val COLUMN_PATH = "path"
        const val COLUMN_SERVICE_ID = "service_id"
        const val COLUMN_SERVICE_NAME = "service_name"
    }

    private val logger = loggerFor(PrajDocumentJDBCDAO::class)

    val simpleJdbcInsert=SimpleJdbcInsert(jdbcTemplate)
            .withTableName(TABLE_NAME)
            .usingGeneratedKeyColumns(COLUMN_ID)

    private object PrajDocumentRowMapper: RowMapper<PrajDocument> {
        override fun mapRow(rs: ResultSet, rowNum: Int): PrajDocument? {
            return PrajDocument(
                    id = rs.getString(1)!!,
                    serviceName = rs.getString(2)!!,
                    serviceId = rs.getString(3)!!,
                    path = rs.getString(4)!!,
                    content = rs.getString(5),
                    tag = rs.getString(6)
            )
        }
    }

    private fun createParameterSource(prajDocument: PrajDocument) = MapSqlParameterSource(mapOf(
            COLUMN_CONTENT      to prajDocument.content,
            COLUMN_PATH         to prajDocument.path,
            COLUMN_SERVICE_ID   to prajDocument.serviceId,
            COLUMN_SERVICE_NAME to prajDocument.serviceName,
            COLUMN_TAG          to prajDocument.tag
    ))


    @Transactional
    override fun delete(doc: PrajDocument):Int = jdbcTemplate.update("DELETE FROM praj_document WHERE id = ?", doc.id!!)

    override fun deleteByServiceId(id: String) {
        jdbcTemplate.update("DELETE FROM $TABLE_NAME WHERE $COLUMN_SERVICE_ID = ?", id)
    }

    override fun updateTag(serviceId: String, tag: String):Int {
        return jdbcTemplate.update(
                """
                    UPDATE $TABLE_NAME t
                    SET t.$COLUMN_TAG = ?
                    WHERE EXISTS (
                        SELECT 1 FROM ${PrajServiceJDBCDAO.TABLE_NAME} s
                        INNER JOIN $TABLE_NAME t2 ON t2.$COLUMN_SERVICE_ID = s.${PrajServiceJDBCDAO.COLUMN_NAME_ID}
                        WHERE s.${PrajServiceJDBCDAO.COLUMN_NAME_ID} = ?
                        AND t2.$COLUMN_ID = t.$COLUMN_ID
                        )
                """.trimIndent(), tag, serviceId)
    }

    @Transactional
    override fun deleteTrackingServiceAndPath(doc: PrajDocument): Int {
        val id:String? = findIdByServiceAndPath(doc.serviceId!!, doc.path.trim())
        return if(id == null){
            logger.warn("Cannot found doc with service='{}' and path='{}'", doc.serviceId, doc.path)
            0
        }else{
            doc.id = id
            delete(doc)
        }
    }

    private fun findIdByServiceAndPath(serviceId: String, path: String):String? =
        try {
            jdbcTemplate.queryForObject(
                    """
                        SELECT d.$COLUMN_ID FROM $TABLE_NAME d
                        INNER JOIN ${PrajServiceJDBCDAO.TABLE_NAME} s ON s.${PrajServiceJDBCDAO.COLUMN_NAME_ID} = d.$COLUMN_SERVICE_ID
                        WHERE s.${PrajServiceJDBCDAO.COLUMN_NAME_ID} = ? AND d.$COLUMN_PATH = ?
                    """.trimMargin(),
                    String::class.java, serviceId, path)
        }catch (e: EmptyResultDataAccessException){
            null
        }


    @Transactional
    override fun saveTrackingServiceAndPath(doc: PrajDocument): PrajDocument {
        doc.id = findIdByServiceAndPath(doc.serviceId!!, doc.path.trim())
        return save(doc)
    }

    @Transactional
    override fun save(doc: PrajDocument):PrajDocument{
        if(doc.id == null || doc.id?.isBlank() == true) {
            doc.id = simpleJdbcInsert.executeAndReturnKey(createParameterSource(doc)).toString()
        }else{
            jdbcTemplate.update(
                    """
                        UPDATE praj_document SET
                            $COLUMN_CONTENT         = ?,
                            $COLUMN_TAG             = ?,
                            $COLUMN_PATH            = ?,
                            $COLUMN_SERVICE_ID      = ?,
                            $COLUMN_SERVICE_NAME    = ?
                        WHERE
                            id = ?
                    """.trimIndent(),
                    doc.content,
                    doc.tag,
                    doc.path,
                    doc.serviceId,
                    doc.serviceName,
                    doc.id
                    )
        }
        return doc
    }

    override fun findByService(id: String):List<PrajDocument> = jdbcTemplate.query("""SELECT
        $COLUMN_ID,
        $COLUMN_SERVICE_NAME,
        $COLUMN_SERVICE_ID,
        $COLUMN_PATH,
        $COLUMN_CONTENT,
        $COLUMN_TAG
        FROM $TABLE_NAME
        WHERE $COLUMN_SERVICE_ID = ?
    """.trimMargin(), arrayOf(id), PrajDocumentRowMapper)
}