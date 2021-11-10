package no.nav.bidrag.cucumber.controller

import no.nav.bidrag.cucumber.TestUtil.assumeThatActuatorHealthIsRunning
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
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
        assumeThatActuatorHealthIsRunningCachedException("https://bidrag-beregn-barnebidrag-rest.dev.adeo.no", "bidrag-beregn-barnebidrag-rest")

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-beregn-barnebidrag-rest.dev.adeo.no@bidrag-beregn-barnebidrag-rest"]
                }
                """.trimMargin().trim(), initJsonAsMediaType()
            ),
            Void::class.java
        )

        assertThat(testResponse.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    @Disabled("høna og egget... kan ikke teste dette før en gyldig deploy")
    fun `skal hente ut cucumber tekst fra kjøring`() {
        assumeThatActuatorHealthIsRunningCachedException("https://bidrag-cucumber-onprem-feature.dev.adeo.no", "bidrag-cucumber-onprem")

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-cucumber-onprem.dev.adeo.no@bidrag-cucumber-onprem"],
                  "sanityCheck":true
                }
                """.trimMargin().trim(), initJsonAsMediaType()
            ),
            String::class.java
        )

        val softly = SoftAssertions()
        softly.assertThat(testResponse.body).`as`("body").contains("Scenarios")
        softly.assertThat(testResponse.body).`as`("body").contains("Failed")
        softly.assertThat(testResponse.body).`as`("body").contains("Passed")
        softly.assertAll()
    }

    @Test
    @Disabled("WIP. kopiert kode. disabled intil videre...")
    fun `skal ikke feile når det er sanity check selv om det sendes med brukernavn til en testbruker`() {
        assumeThatActuatorHealthIsRunningCachedException("https://bidrag-beregn-barnebidrag-rest.dev.adeo.no", "bidrag-beregn-barnebidrag-rest")

        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-beregn-barnebidrag-rest.dev.adeo.no@bidrag-beregn-barnebidrag-rest"]
                  "sanityCheck":true
                }
                """.trimMargin().trim(), initJsonAsMediaType()
            ),
            String::class.java
        )

        assertThat(testResponse.statusCode).`as`("status code").isEqualTo(HttpStatus.OK)
    }

    @Test
    @Disabled("WIP. kopiert kode. disabled intil videre...")
    fun `skal logge eventuelle exception når det feiler under testing`() {
        val testResponse = testRestTemplate.postForEntity(
            "/run",
            HttpEntity(
                """
                {
                  "ingressesForApps":["https://bidrag-beregn-barnebidrag-rest.dev.adeo.no@bidrag-beregn-barnebidrag-rest"]
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
