package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import javax.sql.DataSource

internal class MySqlDialectHelper : SqlDialectHelper {
    override fun createIndexSnippet()= "CREATE FULLTEXT INDEX idx_document_content ON  praj_document(content)"


}