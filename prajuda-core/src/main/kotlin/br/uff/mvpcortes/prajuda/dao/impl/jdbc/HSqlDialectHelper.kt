package br.uff.mvpcortes.prajuda.dao.impl.jdbc

internal class HSqlDialectHelper : SqlDialectHelper {

    override fun createIndexSnippet()="CREATE INDEX idx_praj_document_content ON praj_document (content)"

    override fun updateTagSnipped():String =
            """
                    UPDATE ${PrajDocumentJDBCDAO.TABLE_NAME} doc
                    INNER JOIN ${PrajServiceJDBCDAO.TABLE_NAME} s on s.${PrajServiceJDBCDAO.COLUMN_NAME_ID} = doc.${PrajDocumentJDBCDAO.COLUMN_SERVICE_ID}
                    set doc.tag = ?
                    where s.id = ?
                """.trimIndent()
}