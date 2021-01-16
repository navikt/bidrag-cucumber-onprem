package no.nav.bidrag.cucumber

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EnvironmentTest {

    @Test
    fun `skal hente environment`() {
        System.setProperty(INTEGRATION_INPUT, "src/test/resources/integrationInput.json")
        val miljo = Environment.fetchIntegrationInput().environment

        assertThat(miljo).isEqualTo("main")
    }
}