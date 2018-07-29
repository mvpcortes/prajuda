package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajServiceDAO
import br.uff.mvpcortes.prajuda.model.PrajService
import br.uff.mvpcortes.prajuda.model.RepositoryInfo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
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

}