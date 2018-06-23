package br.uff.mvpcortes.prajuda
import org.slf4j.LoggerFactory

inline fun <T:Any> loggerFor(clazz:Class<T>) = LoggerFactory.getLogger(clazz)
