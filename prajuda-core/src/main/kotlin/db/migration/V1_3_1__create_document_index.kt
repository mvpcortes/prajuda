package db.migration

import br.uff.mvpcortes.prajuda.dao.impl.jdbc.SqlDialectHelper
import br.uff.mvpcortes.prajuda.loggerFor
import org.flywaydb.core.api.migration.spring.SpringJdbcMigration
import org.springframework.jdbc.core.JdbcTemplate

/**
 * Implement
 */
class V1_3_1__create_document_index : SpringJdbcMigration {

    val logger = loggerFor(SpringJdbcMigration::class)

    override fun migrate(jdbcTemplate: JdbcTemplate?) {

        val sqlDialectHelper = SqlDialectHelper.createHelper(jdbcTemplate!!.dataSource!!)

        val sqlCreateIndex = sqlDialectHelper?.createIndexSnippet()

        if(sqlCreateIndex!=null) {
            jdbcTemplate.execute(sqlCreateIndex)
        }else{
            logger.warn("Database does not support content index")
        }

    }
}