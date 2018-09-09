package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

class ReactiveJdbcTemplate(val transactionTemplate: TransactionTemplate, val jdbcTemplate: JdbcTemplate){


    private val DEFAULT_QTD_WINDOW_GET_STRING = 1024L //QTD characters to get string data from mysql

    fun <T> queryForFlux(query:String, args:Array<Any>, rowMapper: RowMapper<T>): Flux<T> {
        return Flux.create<T> {
            runTransaction(query, it, args, rowMapper)
        }
    }


    /**
     * Return a string value divided in a flux using default window size DEFAULT_QTD_WINDOW_GET_STRING
     * @param query query to get String
     * @param querySize query to return string size
     * @param windowSize qtd characters get from database by window (step)
     * @param args query args
     */
    fun queryStringDividedOnFlux(query:String, querySize:String, args:Array<Any>)
            =queryStringDividedOnFlux(query, querySize, DEFAULT_QTD_WINDOW_GET_STRING, args)

    /**
     * Return a string value divided in a flux. Using default length string
     * @param query query to get String
     * @param querySize query to return string size
     * @param windowSize qtd characters get from database by window (step)
     * @param args query args
     */
    fun queryStringDividedOnFlux(query:String,  windowSize:Long, args:Array<Any>)=
            queryStringDividedOnFlux(query, "SELECT LENGTH(str) FROM ($query)", windowSize, args)
    /**
     * Return a string value divided in a flux
     * @param query query to get String
     * @param querySize query to return string size
     * @param windowSize qtd characters get from database by window (step)
     * @param args query args
     */
    fun queryStringDividedOnFlux(query:String, querySize:String, windowSize:Long, args:Array<Any>):Flux<String>{
        val count = queryStringDividedCount(querySize, args)

        return if(count == null || count <= 0L){
            Flux.empty()
        }else {

            Flux.create<String> {
                val windowCount = (count / windowSize)

                try {
                    (0..windowCount).forEach { window ->

                        val subString = queryStringWindow(query, window, windowSize, args)
                        it.next(subString)
                    }
                    it.complete()
                } catch (e: Throwable) {
                    it.error(e)
                }
            }
        }
    }

    /**
     * Use string column name equal to 'str'
     */
    fun queryStringWindow(query: String, window: Long, windowSize:Long, args: Array<Any>): String {

        val arrayArgs:Array<out Any> = arrayOf<Any>(window*windowSize, windowSize, *args)

        return jdbcTemplate.queryForObject("SELECT SUBSTRING (str, ?, ?) FROM ( $query )"
                , String::class.java, *arrayArgs)
    }

    private fun queryStringDividedCount(querySize: String, args: Array<Any>): Long?{
        try {
            return jdbcTemplate.queryForObject(querySize, args, Long::class.java)
        }catch(e: EmptyResultDataAccessException){
            return null
        }
    }

    private fun <T> runTransaction(query: String, sink: FluxSink< in T>, args: Array<Any>, rowMapper: RowMapper<T>): Long {
        return transactionTemplate.execute{ _ ->
            var qtdInternal = 0L
            try {
                jdbcTemplate.query(query, args) {
                    sink.next(rowMapper.mapRow(it, Math.max(qtdInternal, Integer.MAX_VALUE.toLong()).toInt())!!)
                    qtdInternal += 1L
                }
                sink.complete()
            }catch(e:Exception){
                sink.error(e)
            }
            qtdInternal
        }!!
    }
}
