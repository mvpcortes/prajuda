package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper

fun <T> JdbcTemplate.queryForObjectNullable(sql:String, arguments:Array<Any>, rowMapper: RowMapper<T>):T? =
        this.query(sql, arguments, ResultSetExtractor<T>{
            if(it.next()){
                rowMapper.mapRow(it, 0)
            }else{
                null
            }
        })