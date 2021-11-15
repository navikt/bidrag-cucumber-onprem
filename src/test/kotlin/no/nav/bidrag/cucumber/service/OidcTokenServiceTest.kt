package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.model.CucumberTestsModel
import no.nav.bidrag.cucumber.sikkerhet.OidcTokenManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
internal class OidcTokenServiceTest {

    @Autowired
    private lateinit var oidcTokenService: OidcTokenService

    @MockBean
    private lateinit var oidcTokenManagerMock: OidcTokenManager

    @Test
    fun `skal hente token for en bruker på en applikasjon`() {
        CucumberTestsModel(navUsername = "jactor-rises").initCucumberEnvironment()
        whenever(oidcTokenManagerMock.generateToken(eq("jactor-rises"), any())).thenReturn("xyz123")

        val token = oidcTokenService.generateBearerToken("my-app")

        assertThat(token).isEqualTo("Bearer xyz123")
    }
}