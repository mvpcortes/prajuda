package br.uff.mvpcortes.prajuda.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PrajServiceTest{

    val regex = PrajService.REGEX_PATH_NAME_VALIDATION.toRegex()

    val regexNamePath = PrajService.REGEX_PATH_NAME_VALIDATION.toRegex()

    @ParameterizedTest
    @ValueSource(strings = ["", "xuxu ok", "/no/no/no", "/", "////ok", "no//", "nono///"])
    fun `when invalid relative path then should not match`(path:String){
        assertThat(regex.matches(path)).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = ["a", "prajuda", "1", "prajuda_xuxu", "1prajuda", "123", "prajuda423423", "prajuda"])
    fun `when valid relative path then should match`(path:String){
        assertThat(regex.matches(path)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "xuxu ok", "/no/no/no", "/", "////ok", "no//", "nono///", "prajuda/x", "prajuda/1", "prajuda/prajuda1", "prajuda/prajuda/prajuda", "no/prajuda/no/prajuda", "prajuda/1/", "prajuda/", "prajuda/1/prajuda/"])
    fun `when invalid relative name_path then should not match`(path:String){
        assertThat(regexNamePath.matches(path)).isFalse()
    }

    @ParameterizedTest
    @ValueSource(strings = ["a", "prajuda", "1", "prajuda_xuxu", "1prajuda", "123", "prajuda423423" ])
    fun `when valid relative name_path then should match`(path:String){
        assertThat(regexNamePath.matches(path)).isTrue()
    }
}