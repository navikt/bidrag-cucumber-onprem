package no.nav.bidrag.cucumber.onprem

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.Data

@Suppress("unused") // used by cucumber
class TestDataEgenskaper : No {
    init {
        Og("nokkel for testdata {string}") { nokkel: String ->
            CucumberTestRun.thisRun().testData.nokkel = nokkel
            CucumberTestRun.thisRun().testData.dataForNokkel[nokkel] = Data()
        }

        Og("opprett journalpost når den ikke finnes:") { json: String ->
            val nokkel = CucumberTestRun.thisRun().testData.nokkel ?: throw IllegalStateException("Har ingen nøkkel å lage testdata på")
            if (CucumberTestRun.opprettTestdataForNokkel(nokkel)) {
                TestDataManager.opprettTestData(nokkel = nokkel, json = json)
            }
        }
    }
}
