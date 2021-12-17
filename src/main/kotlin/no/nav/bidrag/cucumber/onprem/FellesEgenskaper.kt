package no.nav.bidrag.cucumber.onprem

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import java.util.EnumSet

@Suppress("unused") // used by cucumber
class FellesEgenskaper : No {

    init {
        Gitt("nais applikasjon {string}") { naisApplikasjon: String -> CucumberTestRun.settOppNaisAppTilTesting(naisApplikasjon) }

        Så("skal http status være {int}") { enHttpStatus: Int ->
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "HttpStatus for ${hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}",
                    value = hentRestTjenesteTilTesting().hentHttpStatus(),
                    expectation = HttpStatus.valueOf(enHttpStatus)
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("responsen skal inneholde {string} = {string}") { key: String, value: String ->
            val responseObject = hentRestTjenesteTilTesting().hentResponseSomMap()
            val verdiFraResponse = responseObject[key]

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "json response",
                    value = verdiFraResponse,
                    expectation = value
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("responsen skal ikke inneholde {string} = {string}") { key: String, value: String ->
            val responseObject = hentRestTjenesteTilTesting().hentResponseSomMap()
            val verdiFraResponse = responseObject[key]

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "json response skal ikke ha feltet '$key'",
                    value = verdiFraResponse
                ) { assertThat(it.value).`as`(it.message).isNull() }
            )
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

        Og("så skal responsen være en liste") {
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "respons fra '${hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}' skal være ei liste",
                    value = hentRestTjenesteTilTesting().hentResponse(),
                    expectation = "[{"
                ) { assertThat(it.value as String?).`as`(it.message).startsWith(it.expectation as String) }
            )
        }

        Så("skal responsen inneholde et objekt med navn {string} som har feltet {string} = {string}") { objekt: String, key: String, value: String ->
            val responseObject = hentRestTjenesteTilTesting().hentResponseSomMap()
            @Suppress("UNCHECKED_CAST") val objektFraResponse = responseObject[objekt] as Map<String, Any>?

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "$objekt skal inneholde $key",
                    value = objektFraResponse?.get(key)?.toString(),
                    expectation = value
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.value) }
            )
        }

        Og("responsen skal inneholde et objekt med navn {string} som har feltene:") { objekt: String, felter: DataTable ->
            @Suppress("UNCHECKED_CAST") val objektResponse = hentRestTjenesteTilTesting().hentResponseSomMap()[objekt] as Map<String, *>?
            val manglerFelt = ArrayList<String>()

            if (CucumberTestRun.isNotSanityCheck) {
                felter.asList().forEach { if (!objektResponse?.containsKey(it)!!) manglerFelt.add(it) }

                assertThat(manglerFelt).`as`("Response skal ikke mangle noen av $felter").isEmpty()
            }
        }
    }
}
