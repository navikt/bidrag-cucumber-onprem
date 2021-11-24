package no.nav.bidrag.cucumber.onprem.dokument.arkiv

import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.RestTjeneste
import java.time.LocalDate

object ArkivManager {
    private val dokarkivApp = "dokarkiv-api"

    fun opprettJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer: String, fagomrade: String) {
        val restTjenesteTilTesting = CucumberTestRun.hentRestTjenesteTilTesting()
        restTjenesteTilTesting.exchangeGet("/sak/$saksnummer/journal?fagomrade=$fagomrade")

        if (restTjenesteTilTesting.hentResponseSomListe().isEmpty()) {
            CucumberTestRun.settOppNaisApp(dokarkivApp).exchangePost(
                endpointUrl = "/rest/journalpostapi/v1/journalpost",
                body = """
                {
                  "datoMottatt": "${LocalDate.now().minusDays(1)}",
                  "tittel": "test by bidrag-cucumber-onprem",
                  "journalposttype": "INNGAAENDE",
                  "tema": "BID",
                  "behandlingstema": "BID",
                  "kanal": "NAV_NO",
                  "journalfoerendeEnhet": "0701",
                  "avsenderMottaker": {
                    "id": "06127412345",
                    "idType": "FNR",
                    "navn": "Blund, Jon"
                  },
                  "sak": {
                    "fagsakId": "$saksnummer",
                    "sakstype": "FAGSAK"
                  },
                  "dokumenter": [
                    {
                      "tittel": "En cucumber test",
                      "brevkode": "NAV 04-01.04",
                      "dokumentvarianter": [
                        {
                          "filtype": "PDFA"
                        }
                      ]
                    }
                  ]
                }
                """.trimIndent()
            )
        }
    }
}
