package br.uff.mvpcortes.prajuda.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

class WithIdTest {

    class MyWithId(override val id: String?, val value:String) :WithId


    @Nested
    inner class `when derived class has onlyId called`{
        val myWithId = MyWithId(id="1", value="value")

        val onlyId = myWithId.onlyId()

        @Test
        fun `then onlyId is not same original class`(){
            assertThat(onlyId).isNotSameAs(myWithId)
        }

        @Test
        fun `then onlyId has same id`(){
            assertThat(onlyId.id).isEqualTo(myWithId.id)
        }

        @Test
        fun `then onlyId has only property 'id'`(){
            assertThat(onlyId::class.memberProperties).hasSize(1)
            assertThat(onlyId::class.memberProperties.map{it.name}.single()).isEqualTo("id")
        }
    }



}