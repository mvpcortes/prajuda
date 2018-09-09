package br.uff.mvpcortes.prajuda.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RepositoryInfoTest{

    @Test
    fun `when password is empty then does not have credentials`(){
        val repositoryInfo = RepositoryInfo(password = "")
        assertThat(repositoryInfo.hasCredentials()).isFalse()
    }

    @Test
    fun `when password is spaces then does not have credentials`(){
        val repositoryInfo = RepositoryInfo(password = "   ")
        assertThat(repositoryInfo.hasCredentials()).isFalse()
    }

    @Test
    fun `when password is word then have credentials`(){
        val repositoryInfo = RepositoryInfo(password = "xuxu")
        assertThat(repositoryInfo.hasCredentials()).isTrue()
    }
}