package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.model.fixture.PrajServiceFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PrajServiceTest{

    @Test
    fun `when call removeDocumentDir then remove prefix`(){

        val prajService = PrajServiceFixture.withDocumentDir("123_test")

        val removedPath = prajService.removeDocumentDir("123_test/a/b/c")

        assertThat(removedPath).isEqualTo("a/b/c")
    }
}