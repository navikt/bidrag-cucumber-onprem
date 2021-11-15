package no.nav.bidrag.cucumber.controller

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisplayName("Test of mapping dto from json")
class JsonMappingTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `skal mappe CucumberTestsApi`() {
        val json = """
          {
            "tags": ["@arkiv-swagger"],
            "navUsername": "j103364",
            "testUsername": "z992903",
            "noContextPathForApps": ["oppgave"],
            "ingressesForApps": [
              "https://oppgave-q1.dev-fss-pub.nais.io@no-tag:oppgave",
              "https://bidrag-dokument-arkiv.dev.adeo.no@bidrag-dokument-arkiv"
            ]
          }
          """.trimIndent()

        val cucumberTestsApi = objectMapper.readValue(json, CucumberTestsApi::class.java)

        assertAll(
            { assertThat(cucumberTestsApi).`as`("cucumberTestsApi").isNotNull() },
            { assertThat(cucumberTestsApi.tags).`as`("tags").isEqualTo(listOf("@arkiv-swagger")) },
            { assertThat(cucumberTestsApi.navUsername).`as`("navUsername").isEqualTo("j103364") },
            { assertThat(cucumberTestsApi.testUsername).`as`("testUsername").isEqualTo("z992903") },
            { assertThat(cucumberTestsApi.noContextPathForApps).`as`("noContextPathForApps").isEqualTo(listOf("oppgave")) },
            {
                assertThat(cucumberTestsApi.ingressesForApps).`as`("ingressesForApps").isEqualTo(
                    listOf(
                        "https://oppgave-q1.dev-fss-pub.nais.io@no-tag:oppgave", "https://bidrag-dokument-arkiv.dev.adeo.no@bidrag-dokument-arkiv"
                    )
                )
            }
        )
    }
}
