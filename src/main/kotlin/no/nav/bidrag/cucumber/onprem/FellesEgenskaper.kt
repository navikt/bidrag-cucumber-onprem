package no.nav.bidrag.cucumber.onprem

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService.Assertion
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import java.util.EnumSet

@Suppress("unused") // used by cucumber
class FellesEgenskaper : No {

    init {
        Gitt("nais applikasjon {string}") { naisApplikasjon: String -> CucumberTestRun.settOppNaisAppTilTesting(naisApplikasjon) }

        Så("skal http status være {int}") { enHttpStatus: Int ->
            FellesEgenskaperService.assertWhenNotSanityCheck(
                Assertion(
                    message = "HttpStatus for ${hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}",
                    value = hentRestTjenesteTilTesting().hentHttpStatus(),
                    expectation = HttpStatus.valueOf(enHttpStatus)
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("responsen skal inneholde {string} = {string}") { key: String, value: String ->
            val responseObject = hentRestTjenesteTilTesting().hentResponseSomMap()
            val verdiFraResponse = responseObject[key]?.toString()

            assertThat(verdiFraResponse).`as`("json response").isEqualTo(value)
        }

        Når("det gjøres et kall til {string}") { endpointUrl: String ->
            hentRestTjenesteTilTesting().exchangeGet(endpointUrl)
        }

        Så("skal http status ikke være {int} eller {int}") { enHttpStatus: Int, enAnnenHttpStatus: Int ->
            assertThat(hentRestTjenesteTilTesting().hentHttpStatus())
                .`as`("HttpStatus for " + hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning())
                .isNotIn(EnumSet.of(HttpStatus.valueOf(enHttpStatus), HttpStatus.valueOf(enAnnenHttpStatus)))
        }

        Når("jeg kaller endpoint {string} med parameter {string} = {string}") { endpoint: String, param: String, value: String ->
            hentRestTjenesteTilTesting().exchangeGet("$endpoint?$param=$value")
        }
    }
}
