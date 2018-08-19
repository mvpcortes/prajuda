package br.uff.mvpcortes.prajuda.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PrajServiceTest{

    val regex = PrajService.REGEX_VALIDATION.toRegex()

    @ParameterizedTest
    @ValueSource(strings = arrayOf("", "xuxu ok", "/no/no/no", "/", "////ok", "no//", "nono///" ))
    fun `when invalid relative path then should not match`(path:String){
        assertThat(regex.matches(path)).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf( "a", "prajuda", "1", "prajuda_xuxu", "1prajuda", "123", "prajuda423423", "prajuda/x", "prajuda/1",
            "prajuda/prajuda1", "prajuda/prajuda/prajuda", "no/prajuda/no/prajuda", "prajuda/1/", "prajuda/", "prajuda/1/prajuda/" ))
    fun `when valid relative path then should match`(path:String){
        val path = "prajuda"

        assertThat(regex.matches(path)).isTrue()
    }
}