package br.uff.mvpcortes.prajuda.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HarvesterTypeTest{

    @Test
    fun `verify create HarvesterType`(){
        val ht = HarvesterType("git", "3")

        assertThat(ht.id).isEqualTo("3")
        assertThat(ht.name).isEqualTo("git")
    }
}