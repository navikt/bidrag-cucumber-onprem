package no.nav.bidrag.cucumber.onprem

import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.BidragCucumberSingletons.mapJsonSomMap
import no.nav.bidrag.cucumber.model.BidragCucumberSingletons.mapTilJsonString
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.Data
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.UUID

object TestDataManager {

    private const val bidragTestdata = "bidrag-testdata"

    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(TestDataManager::class.java)

    fun opprettTestDataNarTestdataIkkeErOpprettetTidligere(json: String, nokkel: String) {
        try {
            if (CucumberTestRun.isNotSanityCheck && CucumberTestRun.skalOpprettTestdataForNokkel(nokkel)) {
                val saksnummer = mapJsonSomMap(json)["saksnummer"] as String? ?: throwExceptionWhenJournalfort(json)
                val testDataApp = CucumberTestRun.hentKonfigurertNaisApp(bidragTestdata)
                val jsonMap  = mapJsonSomMap(json)
                // Opprettelse av dokument i brevlager krever unik dokumentreferanse. Dokument kan ikke slettes fra brevlageret
                if (jsonMap["opprettDokument"] == true){
                    jsonMap["dokumentreferanse"] = UUID.randomUUID().toString().replace("-", "").subSequence(0, 15)
                }
                testDataApp.exchangePost(endpointUrl = "/journalpost", body = mapTilJsonString(jsonMap))

                FellesEgenskaperManager.assertWhenNotSanityCheck(
                    Assertion(
                        message = "En journalpost er opprettet",
                        value = testDataApp.hentHttpStatus(),
                        expectation = HttpStatus.CREATED
                    ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
                )

                val responseSomMap = testDataApp.hentResponseSomMap()
                LOGGER.info("Opprettet journalpost for $nokkel: $responseSomMap")

                val fagomrade = mapJsonSomMap(json)["fagomrade"] as String?

                CucumberTestRun.nyeTestData(
                    nokkel = nokkel,
                    journalpostId = (responseSomMap["journalpostId"] as String?) ?: "na",
                    saksnummer = saksnummer,
                    fagomrade = fagomrade
                )
            }
        } catch (t: Throwable) {
            if (CucumberTestRun.isNotSanityCheck) {
                LOGGER.warn("Oppretting av testdata feilet - ${t::class.simpleName}: ${t.message}")
                throw t
            }
        }
    }

    private fun throwExceptionWhenJournalfort(json: String): String? {
        val journalstatus = mapJsonSomMap(json)["journalstatus"] as String? ?: throw IllegalStateException("Ikke journalstatus i $json")
        val saksnummer = mapJsonSomMap(json)["saksnummer"] as String?

        if (saksnummer == null) {
            if (journalstatus == "M") {
                return null
            }

            throw IllegalStateException("Ikke saksnummer i $json")
        }

        return saksnummer
    }

    internal fun hentDataForTest(nokkel: String?): Data {
        return CucumberTestRun.thisRun().testData.dataForNokkel[nokkel] ?: if (CucumberTestRun.isNotSanityCheck)
            throw IllegalStateException("Ingen data for $nokkel")
        else Data()
    }

    fun slettTestData() {
        val headers = HttpHeaders()
        headers.add(CorrelationId.CORRELATION_ID_HEADER, ScenarioManager.createCorrelationIdValue("slett-testdata"))

        CucumberTestRun.hentRestTjenste("bidrag-testdata").exchange(
            jsonEntity = HttpEntity(null, headers),
            endpointUrl = "/journal/slett/testdata",
            httpMethod = HttpMethod.DELETE
        )
    }
}
