package br.uff.mvpcortes.prajuda.dao.impl.jdbc

internal class HSqlDialectHelper : SqlDialectHelper {
    override fun createIndexSnippet()="CREATE INDEX idx_praj_document_content ON praj_document (content)"
}