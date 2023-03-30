package no.nav.bidrag.cucumber.model

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CucumberTestRunTest {

    @Test
    fun `skal hente sanity check`() {
        CucumberTestRun(CucumberTestsApi(sanityCheck = true)).initEnvironment()

        assertThat(CucumberTestRun.isSanityCheck).isTrue

        Environment.initCucumberEnvironment(CucumberTestsModel(sanityCheck = false))
        assertThat(CucumberTestRun.isSanityCheck).isFalse
    }

    @Test
    fun `skal være feature branch når ingressesForApps inneholder -feature`() {
        CucumberTestRun(CucumberTestsApi(ingressesForApps = listOf("https://a-nais-app.no", "https://some-nais-feature.no"))).initEnvironment()

        assertThat(CucumberTestRun.isFeatureBranch).isTrue
    }

    @Test
    fun `skal ikke være feature branch når ingressesForApps mangler -feature`() {
        CucumberTestRun(CucumberTestsApi(ingressesForApps = listOf("https://some-nais-app.no"))).initEnvironment()

        assertThat(CucumberTestRun.isFeatureBranch).isFalse
    }
}
