package no.nav.bidrag.cucumber.onprem.dokument

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestsModel
import no.nav.bidrag.cucumber.model.RestTjeneste
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpStatusCodeException

@SpringBootTest
@DisabledOnOs(value = [OS.LINUX], disabledReason = "spring-context og mocking må ha annen implementasjon i linux... testene nedenfor feiler der...")
internal class DokumentManagerTest {

    @MockkBean(relaxed = true)
    private lateinit var resttjenesteMock: HttpHeaderRestTemplate

    @BeforeEach
    fun `sett opp testkjøring med test resttjeneste`() {
        CucumberTestRun(CucumberTestsModel(ingressesForApps = listOf("https://bullseye@test-rest")).initCucumberEnvironment()).initEnvironment()
    }

    @Test
    fun `skal feile ved 404 kall`() {
        mockNotFoundException()
        val testRest = RestTjeneste.konfigurerResttjeneste("test-rest")

        assertThatExceptionOfType(HttpStatusCodeException::class.java)
            .isThrownBy { testRest.exchangeGet("over-there") }
    }

    @Test
    fun `skal ikke feile ved 404 kall når dette er spesifisert`() {
        mockNotFoundException()
        val testRest = RestTjeneste.konfigurerResttjeneste("test-rest")
        testRest.exchangeGet(
            endpointUrl = "over-there",
            failOnNotFound = false
        )

        assertThat(testRest.hentResponseSomListe()).isEmpty()
    }

    private fun mockNotFoundException() {
        val notFoundException = HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "ikke her",
            HttpHeaders(),
            "".toByteArray(),
            null
        )

        every { resttjenesteMock.exchange(any<String>(), any(), any(), eq(String::class.java)) } throws notFoundException
    }

    @AfterEach
    fun `reset environment`() {
        Environment.reset()
    }
}
