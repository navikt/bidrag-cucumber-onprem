package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.model.CucumberTestsModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Environment")
internal class EnvironmentTest {
    @BeforeEach
    fun `reset Cucumber environment`() = Environment.reset()

    @Test
    fun `skal hente passord basert på testUsername`() {
        val cucumberTestsModel = CucumberTestsModel(testUsername = "jactor-rises")
        cucumberTestsModel.initCucumberEnvironment()

        System.setProperty("TEST_AUTH_JACTOR-RISES", "007")
        assertThat(Environment.testUserAuth).isEqualTo("007")
    }

    @Test
    fun `skal hente passord basert på brukernavn (username)`() {
        CucumberTestsModel(navUsername = "c151787").initCucumberEnvironment()

        System.setProperty("NAV_AUTH_C151787", "707")
        assertThat(Environment.navAuth).isEqualTo("707")
    }
}
