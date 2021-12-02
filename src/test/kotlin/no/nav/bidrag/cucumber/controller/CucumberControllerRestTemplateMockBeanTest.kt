package no.nav.bidrag.cucumber.controller

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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

    @MockBean
    private lateinit var httpHeaderRestTemplateMock: HttpHeaderRestTemplate

    @Test
    fun `skal lage endpoint url mot bidrag-beregn-barnebidrag-sak`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-beregn-barnebidrag-rest.dev.adeo.no@tag:bidrag-beregn-barnebidrag-rest"]
                }
                """.trimMargin().trim(), headers
            ),
            Void::class.java
        )

        val urlCaptor = ArgumentCaptor.forClass(String::class.java)

        verify(httpHeaderRestTemplateMock, atLeastOnce()).exchange(
            urlCaptor.capture(), eq(HttpMethod.GET), any(), eq(String::class.java)
        )

        assertAll(
            { assertThat(testResponse.statusCode).`as`("status code").isEqualTo(HttpStatus.NOT_ACCEPTABLE) },
            {
                assertThat(urlCaptor.value).`as`("endpoint url")
                    .isEqualTo("/swagger-ui/index.html?configUrl=/bidrag-beregn-barnebidrag-rest/v3/api-docs/swagger-config#")
            }
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
                  "ingressesForApps":["https://bidrag-beregn-barnebidrag-rest.dev.adeo.no@tag:bidrag-beregn-barnebidrag-rest"]
                }
                """.trimMargin().trim(), headers
            ),
            String::class.java
        )

        val testMessages = testResponse.body ?: "Ingen body i response: $testResponse"

        assertAll(
            { assertThat(testMessages).contains("Starting") },
            { assertThat(testMessages).contains("Link") },
            { assertThat(testMessages).contains("Finished") },
            { assertThat(testMessages).contains("Scenario") }
        )
    }
}
