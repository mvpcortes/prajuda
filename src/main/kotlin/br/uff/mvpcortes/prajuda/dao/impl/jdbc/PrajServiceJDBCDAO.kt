package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PrajServiceJDBCDAO (val jdbcTemplate:JdbcTemplate): PrajServiceDAO {

    private object PrajServiceRowMapper: RowMapper<PrajService>{
        override fun mapRow(rs: ResultSet, rowNum: Int): PrajService? {
            return PrajService(
                    rs.getString(0),
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    mapRowRepositoryInfo(rs, rowNum),
                    rs.getString(10)
            )
        }

        private fun mapRowRepositoryInfo(rs: ResultSet, rowNum: Int)= RepositoryInfo(
                rs.getString(4),
                rs.getString(5),
                rs.getTimestamp(6).toLocalDateTime(),
                rs.getString(7),
                rs.getString(8),
                rs.getString(9))
    }

    val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
            .withTableName("praj_service")
            .usingGeneratedKeyColumns("id")

    override fun findIds()= jdbcTemplate.queryForList("SELECT id FROM praj_service", String::class.java)

    override fun findByIdNullable(ids: String): PrajService? {
        return jdbcTemplate.queryForObject(
                """SELECT
                        name,
                        url,
                        harvester_type_id,
                        repo_info_uri,
                        repo_info_branch,
                        repo_info_last_modified,
                        repo_info_last_tag,
                        repo_info_username,
                        repo_info_password,
                        document_dir
                    WHERE
                        id in ?
                """.trimIndent(),
                PrajServiceRowMapper,
                arrayOf(ids)
        )
    }

    override fun save(prajService: PrajService): PrajService {
        if (prajService.id == null ) {
            prajService.id = simpleJdbcInsert.executeAndReturnKey(BeanPropertySqlParameterSource(prajService)).toString()
        } else {
            jdbcTemplate.update(
                    """
                    UPDATE praj_service SET
                        name=?,
                        url=?,
                        harvester_type_id=?,
                        repo_info_uri=?,
                        repo_info_branch=?,
                        repo_info_username=?,
                        repo_info_password=?,
                        document_dir=?
                    WHERE
                        id=?

                """.trimIndent(),
                    prajService.name,
                    prajService.url,
                    prajService.harvesterTypeId,
                    prajService.repositoryInfo.uri,
                    prajService.repositoryInfo.branch,
                    prajService.repositoryInfo.username,
                    prajService.repositoryInfo.password,
                    prajService.documentDir,
                    prajService.id
            )
        }
        return prajService
    }

}