package no.nav.bidrag.cucumber.onprem.dokument.arkiv

import no.nav.bidrag.cucumber.Headers.NAV_CALL_ID
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.model.CucumberTestRun
import org.slf4j.LoggerFactory
import java.time.LocalDate

object ArkivManager {
    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(ArkivManager::class.java)

    fun opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer: String, fagomrade: String) {
        val appForBidDokArkiv = "bidrag-dokument-arkiv"
        LOGGER.info("Eksisterer journalpost for saksnummer $saksnummer i $appForBidDokArkiv?")
        val bidragDokumentArkiv = CucumberTestRun.settOppNaisApp(appForBidDokArkiv)

        bidragDokumentArkiv.exchangeGet(
            endpointUrl = "/sak/$saksnummer/journal?fagomrade=$fagomrade",
            failOnNotFound = false
        )

        if (bidragDokumentArkiv.hentResponseSomListe().isEmpty()) {
            val appForDokarkiv = "dokarkiv-api"
            LOGGER.info("Oppretter journalpost med $appForDokarkiv")

            CucumberTestRun.settOppNaisApp(appForDokarkiv).exchangePost(
                endpointUrl = "/rest/journalpostapi/v1/journalpost?forsoekFerdigstill=true",
                body = """
                {
                  "datoMottatt": "${LocalDate.now().minusDays(1)}",
                  "tittel": "test by bidrag-cucumber-onprem",
                  "journalposttype": "INNGAAENDE",
                  "tema": "$fagomrade",
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
                customHeaders = arrayOf(NAV_CALL_ID to ScenarioManager.fetchCorrelationIdForScenario())
            )
        } else {
            LOGGER.info("Fant ${bidragDokumentArkiv.hentResponseSomListe().size} journalpost(er) i $appForBidDokArkiv")
        }
    }
}
