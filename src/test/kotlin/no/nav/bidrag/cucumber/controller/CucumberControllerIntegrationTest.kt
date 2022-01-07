package no.nav.bidrag.cucumber.controller

import no.nav.bidrag.cucumber.TestUtil.assumeThatActuatorHealthIsRunning
import org.assertj.core.api.Assertions.assertThat
import org.junit.AssumptionViolatedException
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("CucumberController (integration test)")
internal class CucumberControllerIntegrationTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    private val ingressIsUp: MutableMap<String, Boolean> = HashMap()

    private fun assumeThatActuatorHealthIsRunningCachedException(ingress: String, app: String) {
        if (!ingressIsUp.contains(ingress)) {
            try {
                assumeThatActuatorHealthIsRunning(ingress, app)
            } catch (ave: AssumptionViolatedException) {
                ingressIsUp[ingress] = false
                throw ave
            } finally {
                ingressIsUp.computeIfAbsent(ingress) { true }
            }
        } else if (ingressIsUp[ingress] == false) {
            throw AssumptionViolatedException("$ingress is not UP")
        }
    }

    @Test
    fun `skal ikke feile ved testing av applikasjon uten sikkerhet`() {
        assumeThatActuatorHealthIsRunningCachedException("https://bidrag-sjablon.dev.adeo.no", "bidrag-sjablon")

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-sjablon.dev.adeo.no@tag:bidrag-sjablon"]
                }
                """.trimMargin().trim(), initJsonAsMediaType()
            ),
            Void::class.java
        )

        assertThat(testResponse.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `skal hente ut cucumber tekst fra kjøring`() {
        assumeThatActuatorHealthIsRunningCachedException("https://bidrag-cucumber-onprem-feature.dev.adeo.no", "bidrag-cucumber-onprem")

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-cucumber-onprem-feature.dev.adeo.no@tag:bidrag-cucumber-onprem"]
                }
                """.trimMargin().trim(), initJsonAsMediaType()
            ),
            String::class.java
        )

        assertAll(
            { assertThat(testResponse.body).`as`("body").contains("Scenarios") },
            { assertThat(testResponse.body).`as`("body").contains("Failed") },
            { assertThat(testResponse.body).`as`("body").contains("Passed") }
        )
    }

    @Test
    fun `skal ikke feile når det er sanity og det sendes med brukernavn til en testbruker`() {
        assumeThatActuatorHealthIsRunningCachedException("https://bidrag-person.dev.adeo.no", "bidrag-person")

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-person.dev.adeo.no@tag:bidrag-person"],
                  "sanityCheck":true, "testUsername": "jumbo"
                }
                """.trimMargin().trim(), initJsonAsMediaType()
            ),
            String::class.java
        )

        assertThat(testResponse.statusCode).`as`("status code").isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `skal logge eventuelle exception når det feiler under testing`() {
        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://i-am-not-here.dev.adeo.no@tag:bidrag-person"]
                }
                """.trimMargin().trim(), initJsonAsMediaType()
            ),
            String::class.java
        )

        assertAll(
            { assertThat(testResponse.statusCode).`as`("status code").isEqualTo(HttpStatus.NOT_ACCEPTABLE) },
            { assertThat(testResponse.body).`as`("body").contains("Failure details:") }
        )
    }

    private fun initJsonAsMediaType(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        return headers
    }
}
