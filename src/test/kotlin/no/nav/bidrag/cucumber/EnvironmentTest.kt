package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.model.CucumberTestsModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Environment")
internal class EnvironmentTest {
    @BeforeEach
    fun `reset Cucumber environment`() = Environment.resetCucumberEnvironment()

    @Test
    fun `skal hente passord basert på testUsername`() {
        val cucumberTestsModel = CucumberTestsModel(testUsername = "jactor-rises")
        cucumberTestsModel.initCucumberEnvironment()

        System.setProperty("TEST_AUTH_JACTOR-RISES", "007")
        assertThat(Environment.testUserAuth).isEqualTo("007")
    }

    @Test
    fun `skal hente passord basert på brukernavn (username)`() {
        CucumberTestsModel(navUsername = "j104364").initCucumberEnvironment()

        System.setProperty("NAV_AUTH_J104364", "707")
        assertThat(Environment.navAuth).isEqualTo("707")
    }

    @Test
    fun `skal hente sanity check`() {
        val cucumberTestsModel = CucumberTestsModel(sanityCheck = true)
        cucumberTestsModel.initCucumberEnvironment()

        assertThat(Environment.isSanityCheck).isTrue

        Environment.initCucumberEnvironment(CucumberTestsModel(sanityCheck = false))
        assertThat(Environment.isSanityCheck).isFalse
    }

    @Test
    fun `skal være feature branch når ingressesForApps inneholder -feature`() {
        CucumberTestsModel(ingressesForApps = listOf("https://a-nais-app.no", "https://some-nais-feature.no")).initCucumberEnvironment()

        assertThat(Environment.isFeatureBranch).isTrue
    }

    @Test
    fun `skal ikke være feature branch når ingressesForApps mangler -feature`() {
        CucumberTestsModel(ingressesForApps = listOf("https://some-nais-app.no")).initCucumberEnvironment()

        assertThat(Environment.isFeatureBranch).isFalse
    }
}
