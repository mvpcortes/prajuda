package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import br.uff.mvpcortes.prajuda.model.WithId
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.util.Arrays.asList


typealias  MAP_PROP<T>  = (T)->Any?

class SaveJDBCTemplate<T:WithId>(
      val jdbcTemplate:JdbcTemplate,
          TABLE_NAME:String,
      vararg pairs:Pair<String, MAP_PROP<T> >) {
    private val simpleJdbcInsert = SimpleJdbcInsert(jdbcTemplate)
            .withTableName(TABLE_NAME)
            .usingGeneratedKeyColumns(WithId.COLUMN_ID)

    private val listFields: List<Pair<String, MAP_PROP<T>>> = pairs.toList()

    private val idFieldMapProp =
            pairs
                    .asSequence()
                    .filter{it.first == WithId.COLUMN_ID}
                    .single()
                    .second

    private val SQL_COLUMN_UPDATE = listFields
            .asSequence()
            .filter { it.first != WithId.COLUMN_ID }
            .map{"    ${it.first} = ?"}.joinToString(",")

    private val SQL_UPDATE =
            """
                UPDATE $TABLE_NAME SET
                $SQL_COLUMN_UPDATE
                WHERE
                    ${WithId.COLUMN_ID} = ?
            """.trimIndent()





    private fun createParameterSource(t: T) =
            MapSqlParameterSource(
                    listFields.asSequence()
                            .map{ Pair(it.first, it.second(t))}
                            .toMap()
            )

    fun save(t: T): T {
        if(t.id == null || t.id?.isBlank() == true) {
            t.id = simpleJdbcInsert.executeAndReturnKey(createParameterSource(t)).toString()
        }else{
            jdbcTemplate.update(
                   SQL_UPDATE,
                   *createUpdateValues(t)
            )
        }
        return t
    }

    private fun createUpdateValues(t: T):Array<*> {
        val fieldsSequence=  listFields.asSequence()
                .filter{it.first != WithId.COLUMN_ID}
                .map{ it.second }
                .map{ it(t) }

        val fields = fieldsSequence +
                asList(idFieldMapProp)
                        .asSequence()
                        .map{it(t)}

        return fields.toList().toTypedArray()

    }

    fun compile() {
        simpleJdbcInsert.compile()
    }
}