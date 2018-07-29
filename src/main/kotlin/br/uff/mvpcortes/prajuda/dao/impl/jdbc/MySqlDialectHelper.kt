package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import javax.sql.DataSource

internal class MySqlDialectHelper : SqlDialectHelper {
    override fun createIndexSnippet()= "CREATE INDEX FULLTEXT praj_document ON (content)"


}