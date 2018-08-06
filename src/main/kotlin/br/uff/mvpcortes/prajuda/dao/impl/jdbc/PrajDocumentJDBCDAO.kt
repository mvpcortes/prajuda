package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.dao.PrajDocumentDAO
import br.uff.mvpcortes.prajuda.model.PrajDocument
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class PrajDocumentJDBCDAO(val jdbcTemplate:JdbcTemplate):PrajDocumentDAO {

    val simpleJdbcInsert=SimpleJdbcInsert(jdbcTemplate)
            .withTableName("praj_document")
            .usingGeneratedKeyColumns("id")

    val namedParameterJdbcTemplate=NamedParameterJdbcTemplate(jdbcTemplate)


    @Transactional
    override fun delete(doc: PrajDocument):Int {
        return jdbcTemplate.update("DELETE FROM praj_document WHERE id = ?", doc.id)
    }

    @Transactional
    override fun updateTag(serviceId: String, tag: String):Int {
        return jdbcTemplate.update("UPDATE praj_document SET tag = ? WHERE id = ?", tag, serviceId)
    }

    @Transactional
    override fun save(doc: PrajDocument):PrajDocument{
        val parameters = BeanPropertySqlParameterSource(doc)
        if(doc.id == null) {
            doc.id = simpleJdbcInsert.executeAndReturnKey(parameters).toString()
        }else{
            namedParameterJdbcTemplate.update(
                    """
                        UPDATE praj_document SET
                            content             = :content,
                            tag                 = :tag,
                            path                = :path,
                            service_id          = :serviceId,
                            service_name        = :serviceName
                        WHERE
                            id = :id
                    """.trimIndent(), parameters);
        }
        return doc
    }
}