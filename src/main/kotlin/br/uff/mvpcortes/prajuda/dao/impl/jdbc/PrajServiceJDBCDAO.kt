package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import javax.annotation.PostConstruct

@Repository
class PrajServiceJDBCDAO (val jdbcTemplate:JdbcTemplate): PrajServiceDAO {

    companion object {
        const val TABLE_NAME = "praj_service"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_URL = "url"
        const val COLUMN_NAME_HARVERSTER_TYPE = "harvester_type_id"
        const val COLUMN_NAME_REPO_INFO_URI = "repo_info_uri"
        const val COLUMN_NAME_REPO_INFO_LAST_MOD = "repo_info_last_modified"
        const val COLUMN_NAME_REPO_INFO_LAST_TAG = "repo_info_last_tag"
        const val COLUMN_NAME_REPO_INFO_BRANCH = "repo_info_branch"
        const val COLUMN_NAME_REPO_INFO_USERNAME = "repo_info_username"
        const val COLUMN_NAME_REPO_INFO_PASSWORD = "repo_info_password"
        const val COLUMN_NAME_DOCUMENT_DIR = "document_dir"
    }

    private object PrajServiceRowMapper: RowMapper<PrajService>{
        override fun mapRow(rs: ResultSet, rowNum: Int): PrajService? {
            return PrajService(
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    mapRowRepositoryInfo(rs),
                    rs.getString(11)
            )
        }

        private fun mapRowRepositoryInfo(rs: ResultSet)= RepositoryInfo(
                rs.getString(5),
                rs.getString(6),
                rs.getTimestamp(7)!!.toLocalDateTime(),
                rs.getString(8),
                rs.getString(9),
                rs.getString(10)
                )
    }

    val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
            .withTableName(TABLE_NAME)
            .usingGeneratedKeyColumns("id")

    

    private fun createParameterSource(prajService:PrajService) = MapSqlParameterSource(mapOf(
                COLUMN_NAME_NAME to prajService.name,
                COLUMN_NAME_URL to prajService.url,
                COLUMN_NAME_HARVERSTER_TYPE to prajService.harvesterTypeId,
                COLUMN_NAME_REPO_INFO_URI to prajService.repositoryInfo.uri,
                COLUMN_NAME_REPO_INFO_BRANCH to prajService.repositoryInfo.branch,
                COLUMN_NAME_REPO_INFO_USERNAME to prajService.repositoryInfo.username,
                COLUMN_NAME_REPO_INFO_PASSWORD to prajService.repositoryInfo.password,
                COLUMN_NAME_DOCUMENT_DIR to prajService.documentDir,
                COLUMN_NAME_REPO_INFO_LAST_MOD to prajService.repositoryInfo.lastModified,
                COLUMN_NAME_REPO_INFO_LAST_TAG to prajService.repositoryInfo.lastTag
        ))

    @PostConstruct
    fun init() {
        simpleJdbcInsert.compile()
    }


    override fun findIds():List<String> = jdbcTemplate.queryForList("SELECT id FROM $TABLE_NAME", String::class.java)

    override fun findByIdNullable(id: String): PrajService? {
        return jdbcTemplate.queryForObject(
                """SELECT
                        $COLUMN_NAME_ID,
                        $COLUMN_NAME_NAME,
                        $COLUMN_NAME_URL,
                        $COLUMN_NAME_HARVERSTER_TYPE,
                        $COLUMN_NAME_REPO_INFO_URI,
                        $COLUMN_NAME_REPO_INFO_BRANCH,
                        $COLUMN_NAME_REPO_INFO_LAST_MOD,
                        $COLUMN_NAME_REPO_INFO_LAST_TAG,
                        $COLUMN_NAME_REPO_INFO_USERNAME,
                        $COLUMN_NAME_REPO_INFO_PASSWORD,
                        $COLUMN_NAME_DOCUMENT_DIR
                    FROM $TABLE_NAME
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
                        $COLUMN_NAME_HARVERSTER_TYPE    = ?,
                        $COLUMN_NAME_REPO_INFO_URI      = ?,
                        $COLUMN_NAME_REPO_INFO_BRANCH   = ?,
                        $COLUMN_NAME_REPO_INFO_LAST_MOD = ?,
                        $COLUMN_NAME_REPO_INFO_LAST_TAG = ?,
                        $COLUMN_NAME_REPO_INFO_USERNAME = ?,
                        $COLUMN_NAME_REPO_INFO_PASSWORD = ?,
                        $COLUMN_NAME_DOCUMENT_DIR       = ?
                    WHERE
                        $COLUMN_NAME_ID                 =?

                """.trimIndent(),
                    prajService.name,
                    prajService.url,
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

}