package no.nav.bidrag.cucumber.onprem.dokument.arkiv

import no.nav.bidrag.cucumber.Headers.NAV_CALL_ID
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.model.CucumberTestRun
import java.time.LocalDate

object ArkivManager {
    private val dokarkivApp = "dokarkiv-api"

    fun opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer: String, fagomrade: String) {
        val restTjenesteTilTesting = CucumberTestRun.hentRestTjenesteTilTesting()
        restTjenesteTilTesting.exchangeGet("/sak/$saksnummer/journal?fagomrade=$fagomrade")

        if (restTjenesteTilTesting.hentResponseSomListe().isEmpty()) {
            CucumberTestRun.settOppNaisApp(dokarkivApp).exchangePost(
                endpointUrl = "/rest/journalpostapi/v1/journalpost?forsoekFerdigstill=true",
                body = """
                {
                  "datoMottatt": "${LocalDate.now().minusDays(1)}",
                  "tittel": "test by bidrag-cucumber-onprem",
                  "journalposttype": "INNGAAENDE",
                  "tema": "BID",
                  "behandlingstema": "ab0322",
                  "kanal": "NAV_NO",
                  "journalfoerendeEnhet": "0701",
                  "avsenderMottaker": {
                    "id": "06127412345",
                    "idType": "FNR",
                    "navn": "Blund, Jon"
                  },
                  "sak": {
                    "fagsakId": "$saksnummer",
                    "sakstype": "FAGSAK",
                    "fagsaksystem": "BISYS"
                  },
                  "bruker": {
                    "id": "11126222671",
                    "idType": "FNR"
                  },
                  "dokumenter": [
                    {
                      "tittel": "En cucumber test",
                      "brevkode": "NAV 04-01.04",
                      "dokumentvarianter": [
                        {
                          "filtype": "PDFA",
                          "fysiskDokument": "U8O4a25hZCBvbSBkYWdwZW5nZXIgdmVkIHBlcm1pdHRlcmluZw==",
                          "variantformat": "ARKIV"
                        }
                      ]
                    }
                  ]
                }
                """.trimIndent(),
                customHeaders = arrayOf(NAV_CALL_ID to ScenarioManager.fetchCorrelationIdForScenario()))
        }
    }
}
