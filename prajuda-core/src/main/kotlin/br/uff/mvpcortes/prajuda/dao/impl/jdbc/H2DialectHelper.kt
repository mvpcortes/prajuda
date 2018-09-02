package br.uff.mvpcortes.prajuda.dao.impl.jdbc

class H2DialectHelper : SqlDialectHelper {
    override fun createIndexSnippet(): String? = null

    override fun updateTagSnipped():String =
            """
                UPDATE ${PrajDocumentJDBCDAO.TABLE_NAME} t
                SET t.${PrajDocumentJDBCDAO.COLUMN_TAG} = ?
                WHERE EXISTS (
                    SELECT 1 FROM ${PrajServiceJDBCDAO.TABLE_NAME} s
                    INNER JOIN ${PrajDocumentJDBCDAO.TABLE_NAME} t2 ON t2.${PrajDocumentJDBCDAO.COLUMN_SERVICE_ID} = s.${PrajServiceJDBCDAO.COLUMN_NAME_ID}
                    WHERE s.${PrajServiceJDBCDAO.COLUMN_NAME_ID} = ?
                    AND t2.${PrajDocumentJDBCDAO.COLUMN_ID} = t.${PrajDocumentJDBCDAO.COLUMN_ID}
                    )
            """.trimIndent()
}
