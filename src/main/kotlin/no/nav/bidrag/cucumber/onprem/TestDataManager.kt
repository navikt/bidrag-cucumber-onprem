package no.nav.bidrag.cucumber.onprem

import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.BidragCucumberSingletons.mapJsonSomMap
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.Data
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

object TestDataManager {

    private const val bidragTestdata = "bidrag-testdata"

    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(TestDataManager::class.java)

    fun opprettTestData(json: String, nokkel: String) {
        val saksnummer = mapJsonSomMap(json)["saksnummer"] as String? ?: throw IllegalStateException("Ikke saksnummer i $json")
        val testDataApp = CucumberTestRun.settOppNaisApp(bidragTestdata)

        testDataApp.exchangePost(endpointUrl = "/journalpost", body = json)

        FellesEgenskaperManager.assertWhenNotSanityCheck(
            Assertion(
                message = "En journalpost er opprettet",
                value = testDataApp.hentHttpStatus(),
                expectation = HttpStatus.CREATED
            ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
        )

        val responseSomMap = testDataApp.hentResponseSomMap()
        LOGGER.info("Opprettet journalpost for $nokkel: $responseSomMap")

        CucumberTestRun.nyeTestData(
            nokkel = nokkel,
            journalpostId = (responseSomMap["journalpostId"] as String?) ?: "na",
            saksnummer = saksnummer
        )
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
            endpointUrl = "/journal/slett/testdata",
            jsonEntity = HttpEntity(null, headers),
            httpMethod = HttpMethod.DELETE
        )
    }
}
