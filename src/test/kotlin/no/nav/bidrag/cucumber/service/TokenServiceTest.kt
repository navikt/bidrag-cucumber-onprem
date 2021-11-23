package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.SECURITY_TOKEN
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import no.nav.bidrag.cucumber.model.CucumberTestRun
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TokenServiceTest {

    private val tokenService = TestTokenService(generatedToken = "my generated token")

    @BeforeEach
    fun `reset environment`() {
        Environment.reset()
    }

    @Test
    fun `skal hente token fra miljø`() {
        System.setProperty(SECURITY_TOKEN, "my environment token")

        assertThat(tokenService.cacheGeneratedToken("some-app")).isEqualTo("my environment token")
    }

    @Test
    fun `skal hente token fra CucumberTestRun`() {
        CucumberTestRun(CucumberTestsApi(securityToken = "my secret token")).initEnvironment()

        assertThat(tokenService.cacheGeneratedToken("some-app")).isEqualTo("my secret token")
    }

    @Test
    fun `skal hente generert token`() {
        assertThat(tokenService.cacheGeneratedToken("some-app")).isEqualTo("my generated token")
    }

    @Test
    fun `skal legge token i CucumberTestRun`() {
        tokenService.cacheGeneratedToken("some-app")

        assertThat(CucumberTestRun.securityToken).isEqualTo("my generated token")
    }

    private class TestTokenService(private val generatedToken: String) : TokenService() {
        override fun generateToken(application: String) = generatedToken
    }
}
