package no.nav.bidrag.cucumber.onprem.beregn

import com.jayway.jsonpath.JsonPath
import io.cucumber.java8.No
import no.nav.bidrag.cucumber.ABSOLUTE_FEATURE_PATH
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperManager
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory
import java.io.File

@Suppress("unused") // used by cucumber
class BeregnEgenskaper : No {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(BeregnEgenskaper::class.java)

        @JvmStatic
        private val BEREGN_RESOURCES = "$ABSOLUTE_FEATURE_PATH/beregn"
    }

    init {
        Når("jeg bruker endpoint {string} med json fra {string}") { endpoint: String, jsonFilePath: String ->
            LOGGER.info("Leser $BEREGN_RESOURCES/$jsonFilePath")
            val jsonFile = File("$BEREGN_RESOURCES/$jsonFilePath")
            val json = jsonFile.readText(Charsets.UTF_8)

            hentRestTjenesteTilTesting().exchangePost(endpoint, json)
        }

        Og("responsen skal inneholde beløpet {string} under stien {string}") { belop: String, sti: String ->
            val response = hentRestTjenesteTilTesting().hentResponse()
            var resultatBelop = parseJson(response, sti) ?: "-1"

            if (resultatBelop.endsWith(".0")) {
                resultatBelop = resultatBelop.removeSuffix(".0")
            }

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Resultatbeløp",
                    value = resultatBelop,
                    expectation = belop
                ) { assertThat(it.expectation).`as`(it.message).isEqualTo(it.value) }
            )
        }

        Og("responsen skal inneholde resultatkoden {string} under stien {string}")
        { resultatkode: String, sti: String ->
            val response = hentRestTjenesteTilTesting().hentResponse()
            val kode = parseJson(response, sti) ?: "null"

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Resultatkode",
                    value = resultatkode,
                    expectation = kode
                ) { assertThat(it.expectation).`as`(it.message).isEqualTo(it.value) }
            )
        }
    }

    private fun parseJson(response: String?, sti: String): String? {
        if (response == null) {
            return null
        }

        val documentContext = JsonPath.parse(response)
        return documentContext.read<Any>(sti).toString()
    }
}
