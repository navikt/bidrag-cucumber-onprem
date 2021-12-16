package no.nav.bidrag.cucumber.onprem.dokument

import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.onprem.dokument.arkiv.ArkivManager
import org.assertj.core.api.SoftAssertions

@Suppress("unused") // used by cucumber
class DokumentEgenskaper : No {
    private lateinit var fagomrade: String
    private lateinit var saksnummer: String

    init {
        Gitt("saksnummer {string} og fagområdet {string}") { saksnummer: String, fagomrade: String ->
            this.saksnummer = saksnummer
            this.fagomrade = fagomrade
        }

        Og("at det finnes en ferdigstilt journalpost i arkiv på fagområdet og saksnummer") {
            ArkivManager.opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer, fagomrade)
        }

        Og("at det finnes en journalført journalpost i midlertidig brevlager på fagområde og saksnummer") {
            DokumentManager.opprettJournalfortJournalpostNarDenIkkeFinnesFraFor(saksnummer, fagomrade)
        }

        Så("skal journalposter fra arkiv og bidrag-dokument-journalpost kombineres") {
            DokumentManager.sjekkAtJournalposterSomHentesErBadeFraArkivOgBidragDokumentJournalpost(fagomrade, saksnummer)
        }

        Og("jeg henter journalpost for nøkkel {string}") { nokkel: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            val saksnummer = CucumberTestRun.thisRun().testData.hentSaksnummer(nokkel)

            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(
                endpointUrl = "/journal/$journalpostId?saksnummer=$saksnummer",
                failOnNotFound = false
            )
        }

        Og("jeg henter journalposter for nøkkel {string} og fagområde") { nokkel: String, fagomrade: String ->
            val saksnummer = CucumberTestRun.thisRun().testData.hentSaksnummer(nokkel)

            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(
                endpointUrl = "/sak/$saksnummer/journal?fagomrade=$fagomrade"
            )
        }

        @Suppress("UNCHECKED_CAST")
        Og("hvert element i listen skal ha følgende properties satt:") { props: DataTable ->
            val verifyer = SoftAssertions()
            val response: List<Map<String, *>> = CucumberTestRun.hentRestTjenesteTilTesting().hentResponseSomListe() as List<Map<String, *>>

            response.forEach { element ->
                props.asList().forEach { verifyer.assertThat(element).`as`("missing $it in jp: ${element["journalpostId"]})").containsKey(it) }
            }

            verifyer.assertAll()
        }

        Gitt("jeg henter journalpost for sak {string} som har id {string}") { saksnummer: String, journalpostId: String ->
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(
                endpointUrl = "/journal/$journalpostId?saksnummer=$saksnummer",
                failOnNotFound = false
            )
        }

        Gitt("saksnummer {string}") { saksnummer: String ->
            this.saksnummer = saksnummer
        }

        Og("jeg henter journalpost som har id for nøkkel {string}") { nokkel: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(endpointUrl = "/journal/$journalpostId?saksnummer=$saksnummer")
        }

        Og("jeg henter journalpost for nokkel {string}") { nokkel: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            val saksnummer = CucumberTestRun.thisRun().testData.hentSaksnummer(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(endpointUrl = "/journal/$journalpostId?saksnummer=$saksnummer")
        }
    }
}
