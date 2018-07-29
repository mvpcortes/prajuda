package db.migration

import br.uff.mvpcortes.prajuda.dao.impl.jdbc.SqlDialectHelper
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration
import org.springframework.jdbc.core.JdbcTemplate

/**
 * Implement
 */
class V1_3_1__create_document_index : SpringJdbcMigration {

    override fun migrate(jdbcTemplate: JdbcTemplate?) {

        val sqlDialectHelper = SqlDialectHelper.createHelper(jdbcTemplate!!.dataSource!!)

        val sqlCreateIndex = sqlDialectHelper.createIndexSnippet()

        jdbcTemplate.execute(sqlCreateIndex)

    }
}