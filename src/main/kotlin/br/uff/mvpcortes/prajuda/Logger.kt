package br.uff.mvpcortes.prajuda
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

inline fun <T:Any> loggerFor(clazz:Class<T>) = LoggerFactory.getLogger(clazz)
inline fun <T:Any> loggerFor(klazz: KClass<T>) = LoggerFactory.getLogger(klazz.java)
