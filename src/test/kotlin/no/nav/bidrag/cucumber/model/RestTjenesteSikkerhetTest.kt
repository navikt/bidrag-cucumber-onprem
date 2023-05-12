package no.nav.bidrag.cucumber.model

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import no.nav.bidrag.cucumber.service.AzureTokenService
import no.nav.bidrag.cucumber.service.StsTokenService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders

@SpringBootTest
internal class RestTjenesteSikkerhetTest {

    @SpykBean
    private lateinit var azureTokenServiceMock: AzureTokenService

    @SpykBean
    private lateinit var stsTokenService: StsTokenService

    @MockkBean(relaxed = true)
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

        every { azureTokenServiceMock.generateToken("nais-app") } returns "of azure-token"
        RestTjeneste.konfigurerResttjeneste("nais-app")

        val generatorCaptor = slot<() -> String>()
        verify { httpHeaderRestTemplateMock.addHeaderGenerator(HttpHeaders.AUTHORIZATION, capture(generatorCaptor)) }
        val valueGenerator = generatorCaptor.captured

        assertAll(
            { assertThat(valueGenerator.invoke()).isEqualTo("Bearer of azure-token") },
            { verify { azureTokenServiceMock.generateToken("nais-app") } }
        )
    }

    @Test
    fun `skal bruke STS token`() {
        CucumberTestsModel(
            CucumberTestsApi(
                ingressesForApps = listOf("https://somewhere@nais-app"),
                testUsername = "jactor-rises",
                tokenType = "STS"
            )
        ).initCucumberEnvironment()

        every { stsTokenService.generateToken("nais-app") } returns "of sts-token"

        RestTjeneste.konfigurerResttjeneste("nais-app")

        val generatorCaptor = slot<() -> String>()
        verify(atLeast = 0) { httpHeaderRestTemplateMock.addHeaderGenerator(HttpHeaders.AUTHORIZATION, capture(generatorCaptor)) }
        val valueGenerator = generatorCaptor.captured

        assertAll(
            { assertThat(valueGenerator.invoke()).isEqualTo("Bearer of sts-token") },
            { verify(exactly = 0) { azureTokenServiceMock.generateToken(allAny()) } }
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

        RestTjeneste.konfigurerResttjeneste("nais-app")

        val generatorCaptor = slot<() -> String>()
        verify { httpHeaderRestTemplateMock.addHeaderGenerator(HttpHeaders.AUTHORIZATION, capture(generatorCaptor)) }
        val valueGenerator = generatorCaptor.captured

        assertThat(valueGenerator.invoke()).isEqualTo("Bearer secured")
    }
}
