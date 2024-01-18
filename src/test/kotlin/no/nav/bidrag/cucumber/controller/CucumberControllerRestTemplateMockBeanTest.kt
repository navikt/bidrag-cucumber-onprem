package no.nav.bidrag.cucumber.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.slot
import io.mockk.verify
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("CucumberController (mocked bean: RestTemplate)")
class CucumberControllerRestTemplateMockBeanTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @MockkBean(relaxed = true)
    private lateinit var httpHeaderRestTemplateMock: HttpHeaderRestTemplate

    @Test
    fun `skal lage endpoint url mot bidrag-sjablon`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-sjablon.intern.dev.nav.no@tag:bidrag-sjablon"]
                }
                """.trimMargin().trim(),
                headers
            ),
            Void::class.java
        )

        val urlCaptor = slot<String>()

        verify {
            httpHeaderRestTemplateMock.exchange(
                capture(urlCaptor),
                eq(HttpMethod.GET),
                any(),
                eq(String::class.java)
            )
        }

        assertAll(
            { assertThat(testResponse.statusCode).`as`("status code").isEqualTo(HttpStatus.NOT_ACCEPTABLE) },
            { assertThat(urlCaptor.captured).`as`("endpoint url").isEqualTo("/actuator/health") }
        )
    }

    @Test
    fun `skal trekke ut logginnslag til egen bønne som brukes i http resultat`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-sjablon.intern.dev.nav.no@tag:bidrag-sjablon"]
                }
                """.trimMargin().trim(),
                headers
            ),
            String::class.java
        )

        val testMessages = testResponse.body ?: "Ingen body i response: $testResponse"

        assertAll(
            { assertThat(testMessages).contains("Starting") },
            { assertThat(testMessages).contains("correlationId") },
            { assertThat(testMessages).contains("logs.adeo.no") },
            { assertThat(testMessages).contains("Finished") },
            { assertThat(testMessages).contains("Scenario") }
        )
    }
}
