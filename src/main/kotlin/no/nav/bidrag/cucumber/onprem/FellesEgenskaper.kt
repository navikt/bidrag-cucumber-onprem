package no.nav.bidrag.cucumber.onprem

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import java.util.EnumSet
import java.util.function.BiFunction

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

        Og("så skal responsen være ei liste") {
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "respons fra '${hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}' skal være ei liste",
                    value = hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "["
                ) { assertThat(it.value as String?).`as`(it.message).startsWith(it.expectation as String) }
            )
        }

        Og("så skal responsen være ei liste med innhold") {
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "respons fra '${hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}' skal være ei liste",
                    value = hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "[{"
                ) { assertThat(it.value as String?).`as`(it.message).startsWith(it.expectation as String) }
            )
        }

        Og("så skal responsen fra {string} inneholde et objekt med navn {string} som har feltet {string} = {string}") { apiName: String, objekt: String, key: String, value: String ->
            val response = CucumberTestRun.hentRestTjenste("oppgave-api").hentResponseSomMap()
            sjekkAtResponseHarObjektMedFelt(response, objekt, key, value)
        }
        Og("så skal responsen inneholde et objekt med navn {string} som har feltet {string} = {string}") { objekt: String, key: String, value: String ->
            sjekkAtResponseHarObjektMedFelt(objekt, key, value)
        }

        Så("skal responsen inneholde et objekt med navn {string} som har feltet {string} = {string}") { objekt: String, key: String, value: String ->
            sjekkAtResponseHarObjektMedFelt(objekt, key, value)
        }

        Og("responsen skal inneholde et objekt med navn {string} som har feltene:") { objekt: String, felter: DataTable ->
            @Suppress("UNCHECKED_CAST") val objektResponse = hentRestTjenesteTilTesting().hentResponseSomMap()[objekt] as Map<String, *>?
            val manglerFelt = ArrayList<String>()

            if (CucumberTestRun.isNotSanityCheck) {
                felter.asList().forEach { if (!objektResponse?.containsKey(it)!!) manglerFelt.add(it) }

                assertThat(manglerFelt).`as`("Response skal ikke mangle noen av $felter").isEmpty()
            }
        }

        Og("så skal responsen inneholde ei liste med et objekt som har feltet {string} = true") { feltnavn: String ->
            @Suppress("UNCHECKED_CAST") val journalposter = hentRestTjenesteTilTesting().hentResponseSomListe() as List<Map<String, *>>
            val testdata = CucumberTestRun.thisRun().testData
            val nokkel = testdata.nokkel ?: throw IllegalStateException("Ingen nøkkel for testdata!")
            val jpIdFraTestdata = testdata.hentJournalpostId(nokkel)

            val journalpost = journalposter.find {
                val jpid = it["journalpostId"]

                jpIdFraTestdata == jpid
            }

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Skal finne journalpost på $nokkel med journalpostId $jpIdFraTestdata",
                    value = journalpost
                ) { assertThat(it.value).`as`(it.message).isNotNull }
            )

            if (journalpost != null) {
                FellesEgenskaperManager.assertWhenNotSanityCheck(
                    Assertion(
                        message = "journalpost er feilfort",
                        value = journalpost[feltnavn],
                        expectation = true
                    ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
                )
            }
        }

        Og("responsen skal inneholde et objekt med navn {string} som har et felt {string} med feltet {string}") { objekt: String, objektFelt: String, felt: String ->
            val journalpostResponse = hentRestTjenesteTilTesting().hentResponseSomMap()
            val journalpostMap = journalpostResponse[objekt] as Map<*, *>?
            val feltMap = journalpostMap?.get(objektFelt)

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Response skal inneholde feltet $felt",
                    value = feltMap,
                    expectation = felt
                ) {
                    @Suppress("UNCHECKED_CAST")
                    assertThat(it.value as Map<String, *>?).`as`(it.message).containsKey(felt)
                }
            )
        }

        Og("responsen skal inneholde et objekt med navn {string} som har et felt {string} med feltene:") { objekt: String, objektFelt: String, forventedeFelter: DataTable ->
            if (CucumberTestRun.isNotSanityCheck) {
                val journalpostResponse = hentRestTjenesteTilTesting().hentResponseSomMap()
                val journalpostMap = journalpostResponse[objekt] as Map<*, *>?
                @Suppress("UNCHECKED_CAST") val reelleFelter = (journalpostMap?.get(objektFelt) as List<Map<*, *>>?)?.first()

                assertThat(reelleFelter).isNotNull
                val manglerFelt = ArrayList<String>()
                forventedeFelter.asList().forEach { if (!reelleFelter!!.containsKey(it)) manglerFelt.add(it) }

                assertThat(manglerFelt).`as`("Response med $objektFelt skal ikke mangle noen av $forventedeFelter").isEmpty()
            }
        }
    }

    private fun sjekkAtResponseHarObjektMedFelt(objekt: String, key: String, value: String) {
        val responseObject = hentRestTjenesteTilTesting().hentResponseSomMap()
        sjekkAtResponseHarObjektMedFelt(responseObject, objekt, key, value)
    }

    private fun sjekkAtResponseHarObjektMedFelt(responseObject: Map<String, Any>, objekt: String, key: String, value: String) {
        @Suppress("UNCHECKED_CAST") val objektFraResponse = responseObject[objekt] as Map<String, Any>?
        FellesEgenskaperManager.assertWhenNotSanityCheck(
            Assertion(
                message = "$objekt skal inneholde $key",
                value = getValueFromMap(objektFraResponse, key)?.toString(),
                expectation = value
            ) { assertThat(it.value).`as`(it.message).isEqualTo(it.value) }
        )
    }

    private fun getValueFromMap(objektFraResponse: Map<String, Any>?, key: String): Any? {
        var responseValue = objektFraResponse
        val segments = key.split(".")
        for (segment in segments){
            val currentValue = responseValue?.get(segment)
            if (currentValue == null || currentValue is String){
                return currentValue
            }
            responseValue = currentValue as Map<String, Any>
        }
        return ""
    }
}
