package no.nav.bidrag.cucumber.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("CucumberTestsModel")
internal class CucumberTestsModelTest {

    @Test
    fun `skal ha verdien false som streng når sanityCheck er null`() {
        val cucumberTestsModel = CucumberTestsModel(sanityCheck = null)

        assertThat(cucumberTestsModel.getSanityCheck()).isEqualTo("false")
    }

    @Test
    fun `skal hente tags basert på ingressesForApps`() {
        val cucumberTestsModel = CucumberTestsModel(ingressesForApps = listOf("https://somewhere.out.there@tag:bidrag-cucumber-onprem"))

        assertThat(cucumberTestsModel.fetchTags()).`as`("cucumberTests.fetchTags").isEqualTo("@bidrag-cucumber-onprem and not @ignored")
    }

    @Test
    fun `skal også bruke tags som ikke er listet i ingressesForApps`() {
        val cucumberTestsModel = CucumberTestsModel(
            ingressesForApps = listOf("https://somewhere.out.there@tag:bidrag-cucumber-onprem"),
            tags = listOf("@bidrag-sjablon")
        )

        assertThat(cucumberTestsModel.fetchTags()).`as`("cucumberTests.fetchTags")
            .isEqualTo("(@bidrag-sjablon or @bidrag-cucumber-onprem) and not @ignored")
    }

    @Test
    fun `skal bare plukke tags fra ingressesForApps`() {
        val cucumberTestsModel = CucumberTestsModel(ingressesForApps = listOf("somewhere@tag:bidrag-cucumber-onprem", "here@:this-app"))

        assertThat(cucumberTestsModel.fetchTags()).`as`("cucumberTests.fetchTags")
            .isEqualTo("@bidrag-cucumber-onprem and not @ignored")
    }

    @Test
    fun `skal feile når tag ikke finnes blant feature files`() {
        val cucumberTestsModel = CucumberTestsModel(ingressesForApps = listOf("somewhere@tag:not-available"))

        assertThatIllegalStateException().isThrownBy { cucumberTestsModel.fetchTags() }
            .withMessageContaining("@not-available er ukjent")
            .withMessageContaining("bidrag-cucumber-onprem.feature")
    }

    @Test
    fun `skal feile hvis det ikke finnes noen tags`() {
        val cucumberTestsModel = CucumberTestsModel(ingressesForApps = listOf("shit@not-available"), tags = emptyList())

        assertThatIllegalStateException().isThrownBy { cucumberTestsModel.fetchTags() }
            .withMessage("Ingen tags er oppgitt. Bruk liste med tags eller liste med ingresser som har prefiksen 'tag:' etter @")
    }

    @Test
    fun `skal ikke hente ut tags dobbelt opp`() {
        val cucumberTestsModel = CucumberTestsModel(
            ingressesForApps = listOf("https://somewhere.out.there@bidrag-cucumber-onprem"),
            tags = listOf("@bidrag-cucumber-onprem")
        )

        assertThat(cucumberTestsModel.fetchTags()).`as`("cucumberTests.fetchTags").isEqualTo("@bidrag-cucumber-onprem and not @ignored")
    }
}
