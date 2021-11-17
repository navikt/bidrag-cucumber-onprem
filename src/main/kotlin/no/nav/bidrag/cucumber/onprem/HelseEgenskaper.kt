package no.nav.bidrag.cucumber.onprem

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjeneste
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService.Assertion
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService.assertWhenNotSanityCheck
import org.assertj.core.api.Assertions.assertThat

@Suppress("unused") // used by cucumber
class HelseEgenskaper : No {
    init {
        Når("jeg kaller helsetjenesten") {
            hentRestTjeneste().exchangeGet("/actuator/health")
        }

        Og("header {string} skal være {string}") { headerName: String, headerValue: String ->
            val headere = hentRestTjeneste().hentHttpHeaders()
            val header = headere[headerName]

            assertWhenNotSanityCheck(
                Assertion(
                    message = "$headerName skal inneholde $headerValue",
                    value = header?.first(),
                    expectation = headerValue
                ) { assertion -> assertThat(assertion.value).`as`(assertion.message).isEqualTo(headerValue) }
            )
        }
    }
}
