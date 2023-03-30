package no.nav.bidrag.cucumber.onprem.dokument

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.cucumber.datatable.DataTable
import io.cucumber.java8.No
import no.nav.bidrag.commons.web.EnhetFilter
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.Data
import no.nav.bidrag.cucumber.onprem.dokument.arkiv.ArkivManager
import org.assertj.core.api.SoftAssertions

@Suppress("unused") // used by cucumber
class DokumentEgenskaper : No {
    private lateinit var fagomrade: String
    private lateinit var saksnummer: String
    companion object {
        var ARKIVER_JOURNALPOST_NOKKEL = "ARKIVER_JOURNALPOST"
    }

    init {
        Gitt("fagområdet {string}") { fagomrade: String ->
            this.fagomrade = fagomrade
        }
        Gitt("saksnummer {string} og fagområdet {string}") { saksnummer: String, fagomrade: String ->
            this.saksnummer = saksnummer
            this.fagomrade = fagomrade
        }

        Når("jeg ber om at en bidrag journalpost arkiveres i Joark") {
            val testData = CucumberTestRun.thisRun().testData
            val nokkel = testData.nokkel ?: throw IllegalStateException("Ingen nøkkel for testdata")
            val jpId = testData.hentJournalpostId(nokkel).replace("BID-", "")

            val restTjeneste = CucumberTestRun.hentKonfigurertNaisApp("bidrag-dokument-arkivering")
            restTjeneste.exchangePost(
                failOnBadRequest = false,
                endpointUrl = "/api/v1/arkivere/journalpost/$jpId",
                body = "{}"
            )
            val jpIdJoark = jacksonObjectMapper().readTree(restTjeneste.hentResponse()).get("jpIdJoark")
            testData.nye(ARKIVER_JOURNALPOST_NOKKEL, Data(joarkJournalpostId = jpIdJoark.asText()))
        }

        Og("bestiller distribusjon av Joark journalpost") {
            val testData = CucumberTestRun.thisRun().testData
            val jpId = testData.hentJoarkJournalpostId(ARKIVER_JOURNALPOST_NOKKEL)

            CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                failOnBadRequest = false,
                endpointUrl = "/journal/distribuer/JOARK-$jpId",
                body = "{\n" +
                    "    \"adresse\": {\n" +
                    "        \"adresselinje1\": \"Batmangata 5A\",\n" +
                    "        \"land\": \"NO\",\n" +
                    "        \"postnummer\": \"0007\",\n" +
                    "        \"poststed\": \"OSLO\"\n" +
                    "    }\n" +
                    "}"
            )
        }

        Og("at det finnes en ferdigstilt journalpost i arkiv på fagområdet og saksnummer") {
            ArkivManager.opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer, fagomrade)
        }

        Og("kaller journalpost kan arkiveres endepunkt") {
            val testData = CucumberTestRun.thisRun().testData
            val jpId = testData.hentJoarkJournalpostId(ARKIVER_JOURNALPOST_NOKKEL)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(
                failOnBadRequest = false,
                endpointUrl = "/journal/distribuer/JOARK-$jpId/enabled"
            )
        }

        Gitt("opprettet joark journalpost på nøkkel {string}:") { nokkel: String, json: String ->
            ArkivManager.opprettJoarkJournalpostNarDenIkkeFinnes(nokkel = nokkel, json = json)
        }

        Og("at det finnes en utgående journalpost i arkiv på fagområdet og saksnummer") {
            ArkivManager.opprettUtgaaendeJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer, fagomrade)
            val testData = CucumberTestRun.thisRun().testData
            val restTjeneste = CucumberTestRun.hentRestTjenste("dokarkiv-api")
            val jpIdJoark = jacksonObjectMapper().readTree(restTjeneste.hentResponse()).get("journalpostId")
            testData.nye(ARKIVER_JOURNALPOST_NOKKEL, Data(joarkJournalpostId = jpIdJoark.asText()))
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

        Og("jeg henter sakjournal for opprettede testdata") {
            val testData = CucumberTestRun.thisRun().testData
            val nokkel = testData.nokkel ?: throw IllegalStateException("Ingen nøkkel for testdata")
            val saksnummer = testData.hentSaksnummer(nokkel)
            val fagomrade = testData.dataForNokkel[nokkel]?.fagomrade ?: throw IllegalStateException("Ingen data for $nokkel")

            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(
                endpointUrl = "/sak/$saksnummer/journal?fagomrade=$fagomrade"
            )
        }

        Når("jeg henter journalposter for nøkkel {string} og fagområde {string}") { nokkel: String, fagomrade: String ->
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
                failOnNotFound = false,
                failOnBadRequest = false
            )
        }

        Gitt("saksnummer {string}") { saksnummer: String ->
            this.saksnummer = saksnummer
        }

        Og("jeg henter journalpost som har id for nøkkel {string}") { nokkel: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(endpointUrl = "/journal/$journalpostId?saksnummer=$saksnummer")
        }

        Gitt("at jeg henter journalpost med path {string}") { endpointUrl: String ->
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(endpointUrl = endpointUrl, failOnNotFound = false, failOnBadRequest = false)
        }

        Og("at jeg henter opprettet journalpost med nøkkel {string}") { nokkel: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(endpointUrl = "/journal/$journalpostId")
        }

        Og("jeg registrerer endring på opprettet journalpost med nøkkel {string}:") { nokkel: String, json: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangePatch(
                endpointUrl = "/journal/$journalpostId",
                journalpostJson = json
            )
        }

        Og("jeg registrerer endring på opprettet journalpost på enhet {string} med nøkkel {string}:") { enhet: String, nokkel: String, json: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangePatch(
                endpointUrl = "/journal/$journalpostId",
                journalpostJson = json,
                customHeaders = arrayOf(EnhetFilter.X_ENHET_HEADER to enhet)
            )
        }

        Og("jeg registrerer endring på opprettet joark journalpost med nøkkel {string}:") { nokkel: String, json: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJoarkJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangePatch(
                endpointUrl = "/journal/JOARK-$journalpostId",
                journalpostJson = json
            )
        }

        Og("jeg registrerer endring på opprettet journalpost med nøkkel {string} og enhet {string}:") { nokkel: String, enhet: String, json: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangePatch(
                endpointUrl = "/journal/$journalpostId",
                journalpostJson = json,
                customHeaders = arrayOf(EnhetFilter.X_ENHET_HEADER to enhet)
            )
        }

        Og("at jeg henter endret journalpost for nøkkel {string}") { nokkel: String ->
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(endpointUrl = "/journal/$journalpostId")
        }

        Og("en journalpostHendelse for nøkkel {string} skal være produsert") { nokkel: String ->
            KafkaManager.sjekkAtJournalpostHendelseErRegistrert(
                journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            )
        }

        Og("jeg henter journalpost") {
            val nokkel = CucumberTestRun.thisRun().testData.nokkel ?: throw IllegalStateException("Ingen nøkkel for testdata")
            val journalpostId = CucumberTestRun.thisRun().testData.hentJournalpostId(nokkel)
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet(endpointUrl = "/journal/$journalpostId")
        }

        Og("hver journal i listen skal ha {string} = {string}") { key: String, value: String ->
            if (CucumberTestRun.isNotSanityCheck) {
                @Suppress("UNCHECKED_CAST")
                val responseObject = CucumberTestRun.hentRestTjenesteTilTesting()
                    .hentResponseSomListe() as List<Map<String, Any>>

                val verifyer = SoftAssertions()

                responseObject.forEach {
                    verifyer.assertThat(it.get(key)).`as`("id: ${it["journalpostId"]}").isEqualTo(value)
                }

                verifyer.assertAll()
            }
        }

        Og("hver journal i listen skal ha objektet {string} med feltene") { objektNavn: String, properties: DataTable ->
            if (CucumberTestRun.isNotSanityCheck) {
                val verifyer = SoftAssertions()

                @Suppress("UNCHECKED_CAST")
                val responseObject = CucumberTestRun.hentRestTjenesteTilTesting()
                    .hentResponseSomListe() as List<Map<String, Map<String, Any>>>

                responseObject.forEach { jp ->
                    val objekt = jp[objektNavn] as Map<String, *>
                    properties.asList().forEach { verifyer.assertThat(objekt).`as`("id: ${jp["journalpostId"]}").containsKey(it) }
                }

                verifyer.assertAll()
            }
        }
    }
}
