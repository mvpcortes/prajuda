package br.uff.mvpcortes.prajuda.dao.impl.jdbc

import org.reactivestreams.Subscriber
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.transaction.support.TransactionTemplate
import reactor.core.publisher.Flux

class ReactiveJdbcTemplate(val transactionTemplate: TransactionTemplate, val jdbcTemplate: JdbcTemplate){



    fun <T> queryForFlux(query:String, args:Array<Any>, rowMapper: RowMapper<T>): Flux<T> {
        return Flux.from<T> { subscriber->
            this.runTransaction(query, args, subscriber, rowMapper)
        }
    }

    private fun <T> runTransaction(query: String, args: Array<Any>, subscriber: Subscriber<in T>, rowMapper: RowMapper<T>): Long {
        return transactionTemplate.execute{ _ ->
            var qtdInternal = 0L
            try {
                jdbcTemplate.query(query, args) {
                    try {
                        subscriber.onNext(rowMapper.mapRow(it, Math.max(qtdInternal, Integer.MAX_VALUE.toLong()).toInt()))
                        qtdInternal+=1L
                    } catch (e: Exception) {
                        throw IllegalStateException("Fail to map row", e)
                    }
                }
                subscriber.onComplete()
            } catch (ee: Exception) {
                subscriber.onError(ee)
            }
            qtdInternal
        }!!
    }
}
