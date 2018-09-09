package br.uff.mvpcortes.prajuda.model

import br.uff.mvpcortes.prajuda.model.fixture.PrajDocumentFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PrajDocumentTest{

    @Test
    fun `verify create document`(){
        val document = PrajDocumentFixture.default()

        assertThat(document.id).isEqualTo("1")
        assertThat(document.tag).isEqualTo("tag")
        assertThat(document.path).isEqualTo("test/main")
        assertThat(document.serviceId).isEqualTo("1")
        assertThat(document.serviceName).isEqualTo("my_service")
        assertThat(document.content).isEqualTo(PrajDocumentFixture.STR_MD_SIMPLE)

    }
}