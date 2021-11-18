package no.nav.bidrag.cucumber.model

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import no.nav.bidrag.cucumber.service.AzureTokenService
import no.nav.bidrag.cucumber.service.OidcTokenService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders

@SpringBootTest
internal class RestTjenesteSikkerhetTest {

    @MockBean
    private lateinit var azureTokenServiceMock: AzureTokenService

    @MockBean
    private lateinit var oidcTokenServiceMock: OidcTokenService

    @MockBean
    private lateinit var httpHeaderRestTemplateMock: HttpHeaderRestTemplate

    @BeforeEach
    fun `reset Environment`() {
        Environment.reset()
    }

    @Test
    fun `skal generere AZURE token`() {
        CucumberTestRun(
            CucumberTestsApi(
                ingressesForApps = listOf("https://somewhere@nais-app"),
                testUsername = "jactor-rises",
                tokenType = "AZURE"
            )
        ).initEnvironment()

        whenever(azureTokenServiceMock.generateToken("nais-app")).thenReturn("of azure-token")
        RestTjeneste("nais-app")

        val generatorCaptor = ArgumentCaptor.forClass(HttpHeaderRestTemplate.ValueGenerator::class.java)
        verify(httpHeaderRestTemplateMock).addHeaderGenerator(eq(HttpHeaders.AUTHORIZATION), generatorCaptor.capture())
        val valueGenerator = generatorCaptor.value

        assertAll(
            { assertThat(valueGenerator.generate()).isEqualTo("Bearer of azure-token") },
            { verify(azureTokenServiceMock).generateToken("nais-app") },
            { verify(oidcTokenServiceMock, never()).generateToken(anyOrNull()) }
        )
    }

    @Test
    fun `skal bruke OIDC token`() {
        CucumberTestsModel(
            CucumberTestsApi(
                ingressesForApps = listOf("https://somewhere@nais-app"),
                testUsername = "jactor-rises"
            )
        ).initCucumberEnvironment()

        whenever(oidcTokenServiceMock.generateToken("nais-app")).thenReturn("of oidc-token")
        RestTjeneste("nais-app")

        val generatorCaptor = ArgumentCaptor.forClass(HttpHeaderRestTemplate.ValueGenerator::class.java)
        verify(httpHeaderRestTemplateMock).addHeaderGenerator(eq(HttpHeaders.AUTHORIZATION), generatorCaptor.capture())
        val valueGenerator = generatorCaptor.value

        assertAll(
            { assertThat(valueGenerator.generate()).isEqualTo("Bearer of oidc-token") },
            { verify(azureTokenServiceMock, never()).generateToken(anyOrNull()) },
            { verify(oidcTokenServiceMock).generateToken("nais-app") }
        )
    }

    @Test
    fun `skal bruke eget securiy token`() {
        CucumberTestsModel(
            CucumberTestsApi(
                ingressesForApps = listOf("https://somewhere@nais-app"),
                testUsername = "jactor-rises",
                securityToken = "secured"
            )
        ).initCucumberEnvironment()

        RestTjeneste("nais-app")

        val generatorCaptor = ArgumentCaptor.forClass(HttpHeaderRestTemplate.ValueGenerator::class.java)
        verify(httpHeaderRestTemplateMock).addHeaderGenerator(eq(HttpHeaders.AUTHORIZATION), generatorCaptor.capture())
        val valueGenerator = generatorCaptor.value

        assertThat(valueGenerator.generate()).isEqualTo("Bearer secured")
    }
}
