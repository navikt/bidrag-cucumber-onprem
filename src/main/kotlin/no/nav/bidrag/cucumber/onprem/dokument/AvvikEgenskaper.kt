package no.nav.bidrag.cucumber.onprem.dokument

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.bidrag.commons.web.EnhetFilter
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.ENHETSNUMMER_FOR_AVVIK
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperManager
import no.nav.bidrag.cucumber.onprem.TestDataManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertAll
import org.slf4j.LoggerFactory

@Suppress("unused") // used by cucumber
class AvvikEgenskaper : No {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(AvvikEgenskaper::class.java)
    }

    init {
        Når("jeg ber om gyldige avviksvalg for opprettet journalpost med nøkkel {string}") { nokkel: String ->
            CucumberTestRun.thisRun().testData.nokkel = nokkel
            val testData = CucumberTestRun.thisRun().testData
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(
                endpointUrl = "/journal/${testData.hentJournalpostId(nokkel)}/avvik?saksnummer=${testData.hentSaksnummer(nokkel)}"
            )
        }

        Når("jeg ber om gyldige avviksvalg for mottaksregistrert journalpost") {
            val testData = CucumberTestRun.thisRun().testData
            val nokkel = testData.nokkel ?: throw IllegalStateException("mangler nøkkel for testdata")

            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(
                endpointUrl = "/journal/${testData.hentJournalpostId(nokkel)}/avvik?journalstatus=M"
            )
        }

        Så("skal listen med avvikstyper inneholde {string}") { avvikstype: String ->
            val avvikstyper = CucumberTestRun.hentRestTjenesteTilTesting().hentResponseSomListeAvStrenger()

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Avvikstyper som hentes skal inneholde $avvikstype",
                    value = avvikstyper,
                    expectation = avvikstype
                ) {
                    @Suppress("UNCHECKED_CAST")
                    assertThat(it.value as List<*>).`as`(it.message).contains(it.expectation)
                }
            )
        }

        Og("så skal listen med avvikstyper ikke inneholde {string}") { avvikstype: String ->
            val avvikstyper = CucumberTestRun.hentRestTjenesteTilTesting().hentResponseSomListeAvStrenger()

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Avvikstyper som hentes skal ikke inneholde $avvikstype",
                    value = avvikstyper,
                    expectation = avvikstype
                ) {
                    @Suppress("UNCHECKED_CAST")
                    assertThat(it.value as List<*>).`as`(it.message).doesNotContain(it.expectation)
                }
            )
        }

        Så("listen med avvikstyper skal kun inneholde:") { avvikstyper: DataTable ->
            val forventedeAvvikstyper = avvikstyper.asList()
            val funnetAvvikstyper = CucumberTestRun.hentRestTjenesteTilTesting().hentResponseSomListeAvStrenger()

            assertAll(
                {
                    FellesEgenskaperManager.assertWhenNotSanityCheck(
                        Assertion(
                            message = "$funnetAvvikstyper vs $forventedeAvvikstyper",
                            value = funnetAvvikstyper,
                            expectation = forventedeAvvikstyper.size
                        ) { assertThat(it.value as List<*>).`as`(it.message).hasSize(it.expectation as Int) }
                    )
                }, {
                    FellesEgenskaperManager.assertWhenNotSanityCheck(
                        Assertion(
                            message = "$funnetAvvikstyper vs $forventedeAvvikstyper",
                            value = funnetAvvikstyper,
                            expectation = forventedeAvvikstyper
                        ) { assertThat(it.value as List<*>).`as`(it.message).containsAll(it.expectation as List<*>) }
                    )
                }
            )
        }

        Gitt("avvikstype {string}") { avvikstype: String ->
            try {
                val nokkel = CucumberTestRun.thisRun().testData.nokkel ?: throw IllegalStateException("ingen nøkkel til å holde data for journalpost")
                TestDataManager.hentDataForTest(nokkel).avvik.avvikstype = avvikstype
            } catch (e: Exception) {
                CucumberTestRun.holdExceptionForTest(e)
            } finally {
                LOGGER.info("Svelger cucumber.io exception?")
            }
        }

        Når("jeg behandler avvik på opprettet journalpost") {
            try {
                val nokkel = CucumberTestRun.thisRun().testData.nokkel
                val data = TestDataManager.hentDataForTest(nokkel)

                CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                    endpointUrl = "/journal/${data.journalpostId}/avvik",
                    customHeaders = arrayOf(EnhetFilter.X_ENHET_HEADER to (data.avvik.avviksdetaljer[ENHETSNUMMER_FOR_AVVIK] ?: "-1")),
                    failOnBadRequest = false,
                    body = """{"avvikType":"${data.avvik.avvikstype}"""" +
                            (data.avvik.mapAvviksdetaljer()?.let { ""","detaljer":$it""" } ?: "") +
                            (data.avvik.hentBeskrivelse()?.let { ""","beskrivelse":"$it"""" } ?: "") +
                            (data.avvik.hentBeskrivelse()?.let { ""","beskrivelse":"$it"""" } ?: "") +
                            """${data.saksnummer?.let { ""","saksnummer":"$it"""" } ?: ""}}"""

                )
            } catch (e: Exception) {
                CucumberTestRun.holdExceptionForTest(e)
            } finally {
                LOGGER.info("Svelger cucumber.io exception?")
            }
        }

        Og("jeg ber om gyldige avviksvalg for journalpost") {
            val nokkel = CucumberTestRun.thisRun().testData.nokkel
            val journalpostId = TestDataManager.hentDataForTest(nokkel).journalpostId
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet("/journal/$journalpostId/avvik")
        }

        Og("listen med avvikstyper skal ikke inneholde {string}") { avvikstype: String ->
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "$avvikstype er ikke forventet i gyldige avvik",
                    value = CucumberTestRun.hentRestTjenesteTilTesting().hentResponseSomListe(),
                    expectation = avvikstype
                ) { assertThat(it.value as List<*>).`as`(it.message).doesNotContain(it.expectation) }
            )
        }

        Og("avvikstypen har beskrivelse {string}") { beskrivelse: String ->
            val nokkel = CucumberTestRun.thisRun().testData.nokkel ?: throw IllegalStateException("Ingen nøkkel for testdata")
            val avviksdata = TestDataManager.hentDataForTest(nokkel).avvik
            avviksdata.beskrivelse = beskrivelse
        }

        Og("avviksdetaljer {string} = {string}") { detalj: String, detaljverdi: String ->
            val nokkel = CucumberTestRun.thisRun().testData.nokkel
            val avviksdata = TestDataManager.hentDataForTest(nokkel).avvik
            avviksdata.avviksdetaljer[detalj] = detaljverdi
        }
    }
}
