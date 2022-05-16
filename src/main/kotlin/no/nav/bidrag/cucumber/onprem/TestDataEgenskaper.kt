package no.nav.bidrag.cucumber.onprem

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.Data

@Suppress("unused") // used by cucumber
class TestDataEgenskaper : No {
    init {
        Og("nøkkel for testdata {string}") { nokkel: String ->
            CucumberTestRun.thisRun().testData.nokkel = nokkel
            CucumberTestRun.thisRun().testData.dataForNokkel[nokkel] = Data()
        }

        Og("opprett journalpost når den ikke finnes:") { json: String ->
            val nokkel = CucumberTestRun.thisRun().testData.nokkel ?: throw IllegalStateException("Har ingen nøkkel å lage testdata på")
            TestDataManager.opprettTestDataNarTestdataIkkeErOpprettetTidligere(nokkel = nokkel, json = json)
        }

        Og("lag bidragssak {string} når den ikke finnes fra før:") { saksnummer: String, bidragssakJson: String ->
            CucumberTestRun.hentKonfigurertNaisApp("bidrag-testdata").exchangePost("/sak/$saksnummer", bidragssakJson)
        }

        Gitt("jeg endrer journalpost med nøkkel {string}:") { nokkel: String, journalpostJson: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)

            CucumberTestRun.hentRestTjenesteTilTesting().exchangePatch(
                endpointUrl = "/journal/$journalpostId",
                journalpostJson = journalpostJson
            )
        }

         Gitt("opprettet journalpost på nøkkel {string}:") { nokkel: String, json: String ->
            TestDataManager.opprettTestDataNarTestdataIkkeErOpprettetTidligere(nokkel = nokkel, json = json)
        }
    }
}
