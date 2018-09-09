package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * Interface to define a set of commons sql snippets to use in queries. It will be used database
 */
interface SqlDialectHelper {

    fun createIndexSnippet():String?

    fun updateTagSnipped():String
}

@Configuration
class SqlDialectHelperFactory{

    @Bean("sqlDialectHelper")
    fun createHelper(dataSource: DataSource):SqlDialectHelper{
        val dbName = dataSource.connection.use { it.metaData.databaseProductName }.toUpperCase().trim()

        return when (dbName) {
            "HSQL DATABASE ENGINE"  -> HSqlDialectHelper()
            "H2"    -> H2DialectHelper()
            "MYSQL" -> MySqlDialectHelper()
            else    -> throw IllegalStateException("Cannot createHelper dialectHelper for $dbName")
        }
    }
}