package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.SECURITY_TOKEN
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import no.nav.bidrag.cucumber.model.CucumberTestRun
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TokenServiceTest {

    private val tokenService = TestTokenService("of my generated token")

    @BeforeEach
    fun `reset environment`() {
        Environment.reset()
    }

    @Test
    fun `skal hente token fra miljø`() {
        System.setProperty(SECURITY_TOKEN, "of my environment token")

        assertThat(tokenService.fetchBearerToken("some-app")).isEqualTo("Bearer of my environment token")
    }

    @Test
    fun `skal hente token fra CucumberTestRun`() {
        CucumberTestRun(CucumberTestsApi(securityToken = "of my secret token")).initEnvironment()

        assertThat(tokenService.fetchBearerToken("some-app")).isEqualTo("Bearer of my secret token")
    }

    @Test
    fun `skal hente generated token`() {
        assertThat(tokenService.fetchBearerToken("some-app")).isEqualTo("Bearer of my generated token")
    }

    @Test
    fun `skal oppdatere kjøredata med generated token`() {
        tokenService.fetchBearerToken("some-app")

        assertThat(CucumberTestRun.securityToken).isEqualTo("of my generated token")
    }

    private class TestTokenService(private val generatedToken: String) : TokenService() {
        override fun generateToken(application: String) = generatedToken
    }
}
