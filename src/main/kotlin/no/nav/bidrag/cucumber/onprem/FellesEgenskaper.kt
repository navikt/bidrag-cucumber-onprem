package no.nav.bidrag.cucumber.onprem

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjeneste
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService.Assertion
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import java.util.EnumSet

@Suppress("unused") // used by cucumber
class FellesEgenskaper : No {

    init {
        Gitt("nais applikasjon {string}") { naisApplikasjon: String -> CucumberTestRun.settOppNaisApp(naisApplikasjon) }

        Så("skal http status være {int}") { enHttpStatus: Int ->
            FellesEgenskaperService.assertWhenNotSanityCheck(
                Assertion(
                    "HttpStatus for ${hentRestTjeneste().hentFullUrlMedEventuellWarning()}",
                    hentRestTjeneste().hentHttpStatus(),
                    HttpStatus.valueOf(enHttpStatus),
                    this::harForventetVerdi
                )
            )
        }

        Og("responsen skal inneholde {string} = {string}") { key: String, value: String ->
            val responseObject = hentRestTjeneste().hentResponseSomMap()
            val verdiFraResponse = responseObject[key]?.toString()

            assertThat(verdiFraResponse).`as`("json response").isEqualTo(value)
        }

        Når("det gjøres et kall til {string}") { endpointUrl: String ->
            hentRestTjeneste().exchangeGet(endpointUrl)
        }

        Så("skal http status ikke være {int} eller {int}") { enHttpStatus: Int, enAnnenHttpStatus: Int ->
            assertThat(hentRestTjeneste().hentHttpStatus())
                .`as`("HttpStatus for " + hentRestTjeneste().hentFullUrlMedEventuellWarning())
                .isNotIn(EnumSet.of(HttpStatus.valueOf(enHttpStatus), HttpStatus.valueOf(enAnnenHttpStatus)))
        }

        Når("jeg kaller endpoint {string} med parameter {string} = {string}") { endpoint: String, param: String, value: String ->
            hentRestTjeneste().exchangeGet("$endpoint?$param=$value")
        }

        Så("så skal responsen være ei tom liste") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                Assertion(
                    "Respons fra ${hentRestTjeneste().hentFullUrlMedEventuellWarning()}",
                    hentRestTjeneste().hentResponse()?.trim(),
                    "[]",
                    this::harForventetVerdi
                )
            )
        }
    }

    private fun harForventetVerdi(assertion: Assertion) {
        assertThat(assertion.value).`as`(assertion.message).isEqualTo(assertion.expectation)
    }
}
