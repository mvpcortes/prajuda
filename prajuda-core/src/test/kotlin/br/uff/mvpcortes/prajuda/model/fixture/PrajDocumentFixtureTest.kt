package br.uff.mvpcortes.prajuda.model.fixture

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PrajDocumentFixtureTest{
    @Nested
    inner class `A new PrajDocument is created`(){
        val document = PrajDocumentFixture.new()

        @Test
        fun `should have id null`(){
            assertThat(document.id).isNull()
        }

        @Test
        fun `should have tag 'tag'`(){
            assertThat(document.tag).isEqualTo("tag")
        }

        @Test
        fun `should have path 'test main'`(){
            assertThat(document.path).isEqualTo("test/main")
        }

        @Test
        fun `should have serviceId '1'`(){
            assertThat(document.serviceId).isEqualTo("1")
        }

        @Test
        fun `should have serviceName 'my_service'`(){
            assertThat(document.serviceName).isEqualTo("my_service")
        }

        @Test
        fun `should have content  STR_MD_SIMPLE`(){
            assertThat(document.content).isEqualTo(PrajDocumentFixture.STR_MD_SIMPLE)
        }
    }
}