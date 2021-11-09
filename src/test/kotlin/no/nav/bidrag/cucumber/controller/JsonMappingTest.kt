package no.nav.bidrag.cucumber.controller

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.cucumber.model.CucumberTestsModel
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
    fun `skal mappe en kjøring av bidrag-arberdsflyt`() {
        val json = """
          {
            "tags":["@arbeidsflyt-endre-fagomrade"],
            "testUsername":"z992903",
            "noContextPathForApps":["oppgave"],
            "ingressesForApps":["https://oppgave-q1.dev-fss-pub.nais.io@no-tag:oppgave"]
          }
          """.trimIndent()

        val cucumberTestsModel = objectMapper.readValue(json, CucumberTestsModel::class.java)

        assertAll(
            { assertThat(cucumberTestsModel).`as`("cucumberTestsDto").isNotNull() },
            { assertThat(cucumberTestsModel.tags).`as`("tags").isEqualTo(listOf("@arbeidsflyt-endre-fagomrade")) },
            { assertThat(cucumberTestsModel.testUsername).`as`("testUsername").isEqualTo("z992903") },
            { assertThat(cucumberTestsModel.noContextPathForApps).`as`("noContextPathForApps").isEqualTo(listOf("oppgave")) },
            {
                assertThat(cucumberTestsModel.ingressesForApps).`as`("ingressesForApps")
                    .isEqualTo(listOf("https://oppgave-q1.dev-fss-pub.nais.io@no-tag:oppgave"))
            }
        )
    }
}
