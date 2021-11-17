package no.nav.bidrag.cucumber.model

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import no.nav.bidrag.cucumber.service.AzureTokenService
import no.nav.bidrag.cucumber.service.OidcTokenService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
internal class RestTjenesteSikkerhetTest {

    @MockBean
    private lateinit var azureTokenServiceMock: AzureTokenService

    @MockBean
    private lateinit var oidcTokenServiceMock: OidcTokenService

    @BeforeEach
    fun `reset Environment`() {
        Environment.resetCucumberEnvironment()
    }


    @Test
    fun `skal generere AZURE token ved kall med url`() {
        CucumberTestRun(
            CucumberTestsApi(
                ingressesForApps = listOf("https://somewhere@nais-app"),
                testUsername = "jactor-rises",
                tokenType = "AZURE"
            )
        ).initEnvironment()

        val restTjeneste = RestTjeneste("nais-app")

        assertThrows<RuntimeException> { restTjeneste.exchangeGet("out-there") } // url eksisterer ikke...

        assertAll(
            { verify(azureTokenServiceMock).generateBearerToken("nais-app") },
            { verify(oidcTokenServiceMock, never()).generateBearerToken(anyOrNull()) }
        )
    }

    @Test
    fun `skal generere OIDC token ved kall med url som default når tokenType ikke er spesifisert`() {
        CucumberTestsModel(
            CucumberTestsApi(
                ingressesForApps = listOf("https://somewhere@nais-app"),
                testUsername = "jactor-rises"
            )
        ).initCucumberEnvironment()

        val restTjeneste = RestTjeneste("nais-app")

        assertThrows<RuntimeException> { restTjeneste.exchangeGet("out-there") } // url eksisterer ikke...

        assertAll(
            { verify(azureTokenServiceMock, never()).generateBearerToken(anyOrNull()) },
            { verify(oidcTokenServiceMock).generateBearerToken("nais-app") }
        )
    }
}