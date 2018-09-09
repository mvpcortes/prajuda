package db.migration

import br.uff.mvpcortes.prajuda.dao.impl.jdbc.SqlDialectHelper
import br.uff.mvpcortes.prajuda.dao.impl.jdbc.SqlDialectHelperFactory
import com.nhaarman.mockito_kotlin.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@DisplayName("At Migration createHelper document ")
internal class V1_3_1__create_document_indexTest{

    val sqlDialectHelper:SqlDialectHelper = mock{
        on{createIndexSnippet()}.thenReturn("CREATE INDEX")
    }

    val sqlDialectHelperFactory:SqlDialectHelperFactory = mock{
        on{ createHelper(any())}.thenReturn(sqlDialectHelper)
    }

    val dataSource: DataSource = mock{}

    val jdbcTemplate:JdbcTemplate = mock{
        on{getDataSource()}.thenReturn(dataSource)
    }

    @Test
    fun `when template does not have datasource then fail`(){

        val sqlDialectHelperFactory:SqlDialectHelperFactory = mock{
            on{ createHelper(any())}.thenReturn(null)
        }


        Assertions.assertThrows(NullPointerException::class.java) {
            val migration = V1_3_1__create_document_index(sqlDialectHelperFactory)

            migration.migrate(jdbcTemplate)
        }
    }


    @Test
    fun `when exists helper to datasource and sql index then execute create index`(){
        val migration = V1_3_1__create_document_index(sqlDialectHelperFactory)

        migration.migrate(jdbcTemplate)

        verify(jdbcTemplate, times(1)).execute("CREATE INDEX")
    }

    @Test
    fun `when exists helper to datasource but not sql index then not execute create index`(){

        doReturn(null).whenever(sqlDialectHelper).createIndexSnippet()

        val migration = V1_3_1__create_document_index(sqlDialectHelperFactory)

        migration.migrate(jdbcTemplate)

        verify(jdbcTemplate, never()).execute(any())
    }
}