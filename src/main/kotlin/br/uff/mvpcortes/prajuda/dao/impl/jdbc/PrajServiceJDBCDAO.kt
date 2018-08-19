package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import java.sql.ResultSet
import javax.annotation.PostConstruct

@Repository
class PrajServiceJDBCDAO (val jdbcTemplate:JdbcTemplate,
                          val transactionTemplate: TransactionTemplate,
                          val reactiveJdbcTemplate: ReactiveJdbcTemplate= ReactiveJdbcTemplate(transactionTemplate, jdbcTemplate)
): PrajServiceDAO {

    companion object {
        const val TABLE_NAME = "praj_service"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_URL = "url"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_HARVESTER_TYPE = "harvester_type_id"
        const val COLUMN_NAME_REPO_INFO_URI = "repo_info_uri"
        const val COLUMN_NAME_REPO_INFO_LAST_MOD = "repo_info_last_modified"
        const val COLUMN_NAME_REPO_INFO_LAST_TAG = "repo_info_last_tag"
        const val COLUMN_NAME_REPO_INFO_BRANCH = "repo_info_branch"
        const val COLUMN_NAME_REPO_INFO_USERNAME = "repo_info_username"
        const val COLUMN_NAME_REPO_INFO_PASSWORD = "repo_info_password"
        const val COLUMN_NAME_DOCUMENT_DIR = "document_dir"
        const val STR_SELECT_PROJECTION = """SELECT
                        $COLUMN_NAME_ID,
                        $COLUMN_NAME_NAME,
                        $COLUMN_NAME_URL,
                        $COLUMN_NAME_DESCRIPTION,
                        $COLUMN_NAME_HARVESTER_TYPE,
                        $COLUMN_NAME_REPO_INFO_URI,
                        $COLUMN_NAME_REPO_INFO_BRANCH,
                        $COLUMN_NAME_REPO_INFO_LAST_MOD,
                        $COLUMN_NAME_REPO_INFO_LAST_TAG,
                        $COLUMN_NAME_REPO_INFO_USERNAME,
                        $COLUMN_NAME_REPO_INFO_PASSWORD,
                        $COLUMN_NAME_DOCUMENT_DIR
                    FROM $TABLE_NAME
                    """
    }

    private object PrajServiceRowMapper: RowMapper<PrajService>{
        override fun mapRow(rs: ResultSet, rowNum: Int): PrajService? {
            return PrajService(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    mapRowRepositoryInfo(rs),
                    rs.getString(12)
            )
        }

        private fun mapRowRepositoryInfo(rs: ResultSet)= RepositoryInfo(
                rs.getString(6),
                rs.getString(7),
                rs.getTimestamp(8)!!.toLocalDateTime(),
                rs.getString(9),
                rs.getString(10),
                rs.getString(11)
                )
    }

    val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
            .withTableName(TABLE_NAME)
            .usingGeneratedKeyColumns("id")

    

    private fun createParameterSource(prajService:PrajService) = MapSqlParameterSource(mapOf(
                COLUMN_NAME_NAME                to prajService.name,
                COLUMN_NAME_URL                 to prajService.url,
                COLUMN_NAME_DESCRIPTION         to prajService.description,
                COLUMN_NAME_HARVESTER_TYPE      to prajService.harvesterTypeId,
                COLUMN_NAME_REPO_INFO_URI       to prajService.repositoryInfo.uri,
                COLUMN_NAME_REPO_INFO_BRANCH    to prajService.repositoryInfo.branch,
                COLUMN_NAME_REPO_INFO_USERNAME  to prajService.repositoryInfo.username,
                COLUMN_NAME_REPO_INFO_PASSWORD  to prajService.repositoryInfo.password,
                COLUMN_NAME_DOCUMENT_DIR        to prajService.documentDir,
                COLUMN_NAME_REPO_INFO_LAST_MOD  to prajService.repositoryInfo.lastModified,
                COLUMN_NAME_REPO_INFO_LAST_TAG  to prajService.repositoryInfo.lastTag
        ))

    @PostConstruct
    fun init() {
        simpleJdbcInsert.compile()
    }


    override fun findIds():List<String> = jdbcTemplate.queryForList("SELECT id FROM $TABLE_NAME", String::class.java)

    override fun findByIdNullable(id: String): PrajService? {
        return jdbcTemplate.queryForObject(
                STR_SELECT_PROJECTION+
                """
                    WHERE
                        $COLUMN_NAME_ID = ?
                """.trimIndent(),
                PrajServiceRowMapper,
                id.toLong()
        )
    }


    override fun save(prajService: PrajService): PrajService {
        if (prajService.id == null || prajService.id?.isBlank() == true) {
            prajService.id = simpleJdbcInsert.executeAndReturnKey(createParameterSource(prajService)).toString()
            return prajService
        } else {
            jdbcTemplate.update(
                    """
                    UPDATE $TABLE_NAME SET
                        $COLUMN_NAME_NAME               = ?,
                        $COLUMN_NAME_URL                = ?,
                        $COLUMN_NAME_DESCRIPTION        = ?,
                        $COLUMN_NAME_HARVESTER_TYPE     = ?,
                        $COLUMN_NAME_REPO_INFO_URI      = ?,
                        $COLUMN_NAME_REPO_INFO_BRANCH   = ?,
                        $COLUMN_NAME_REPO_INFO_LAST_MOD = ?,
                        $COLUMN_NAME_REPO_INFO_LAST_TAG = ?,
                        $COLUMN_NAME_REPO_INFO_USERNAME = ?,
                        $COLUMN_NAME_REPO_INFO_PASSWORD = ?,
                        $COLUMN_NAME_DOCUMENT_DIR       = ?
                    WHERE
                        $COLUMN_NAME_ID                 = ?

                """.trimIndent(),
                    prajService.name,
                    prajService.url,
                    prajService.description,
                    prajService.harvesterTypeId,
                    prajService.repositoryInfo.uri,
                    prajService.repositoryInfo.branch,
                    prajService.repositoryInfo.lastModified,
                    prajService.repositoryInfo.lastTag,
                    prajService.repositoryInfo.username,
                    prajService.repositoryInfo.password,
                    prajService.documentDir,
                    prajService.id
            )
        }
        return prajService
    }

    override fun findPage(page: Int, pageSize: Int): Flux<PrajService>{
        return reactiveJdbcTemplate.queryForFlux(
            STR_SELECT_PROJECTION +
                """
                    ORDER BY $COLUMN_NAME_ID ASC
                    LIMIT ? OFFSET ?
                """.trimIndent(),
                arrayOf(pageSize, page*pageSize) as Array<Any>,
                PrajServiceRowMapper
        )
    }


    override fun count()=
        jdbcTemplate.queryForObject(
                "SELECT count($COLUMN_NAME_ID) FROM $TABLE_NAME",
                emptyArray(),
                Long::class.java)

    override fun findByIds(vararg ids: String): Flux<PrajService>{
        assert(ids.isNotEmpty()) {"ids should be greater than 0"}
        return reactiveJdbcTemplate.queryForFlux(
                """
                $STR_SELECT_PROJECTION
                WHERE id in (${ids.asSequence().map{"?"}.joinToString()})
                """.trimIndent(),
                ids as Array<Any>,
                PrajServiceRowMapper
        )
    }
}