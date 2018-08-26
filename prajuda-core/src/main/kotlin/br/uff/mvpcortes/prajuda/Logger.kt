package br.uff.mvpcortes.prajuda
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun <T:Any> loggerFor(clazz:Class<T>): Logger = LoggerFactory.getLogger(clazz)
fun <T:Any> loggerFor(klazz: KClass<T>):Logger = LoggerFactory.getLogger(klazz.java)
