package br.uff.mvpcortes.prajuda.dao.highapi

import com.fasterxml.jackson.databind.ObjectMapper

inline fun <reified T> ObjectMapper.readValue(byteArray:ByteArray)=this.readValue(byteArray, T::class.java)