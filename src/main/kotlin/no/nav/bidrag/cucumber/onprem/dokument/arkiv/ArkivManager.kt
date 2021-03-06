package no.nav.bidrag.cucumber.onprem.dokument.arkiv

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.bidrag.cucumber.Headers.NAV_CALL_ID
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.model.BidragCucumberSingletons
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.Data
import org.slf4j.LoggerFactory
import java.time.LocalDate

object ArkivManager {
    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(ArkivManager::class.java)

    fun opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer: String, fagomrade: String) {
        val appForBidDokArkiv = "bidrag-dokument-arkiv"
        LOGGER.info("Eksisterer journalpost for saksnummer $saksnummer i $appForBidDokArkiv?")
        val bidragDokumentArkiv = CucumberTestRun.hentKonfigurertNaisApp(appForBidDokArkiv)

        bidragDokumentArkiv.exchangeGet(
            endpointUrl = "/sak/$saksnummer/journal?fagomrade=$fagomrade",
            failOnNotFound = false
        )

        if (bidragDokumentArkiv.hentResponseSomListe().isEmpty()) {
            val appForDokarkiv = "dokarkiv-api"
            LOGGER.info("Oppretter journalpost med $appForDokarkiv")

            CucumberTestRun.hentKonfigurertNaisApp(appForDokarkiv).exchangePost(
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

    fun opprettJoarkJournalpostNarDenIkkeFinnes(nokkel: String, json: String) {
        if (CucumberTestRun.isNotSanityCheck && CucumberTestRun.skalOpprettTestdataForNokkel(nokkel)){
            val appForDokarkiv = "dokarkiv-api"
            LOGGER.info("Oppretter journalpost med $appForDokarkiv")

            val jsonMap = BidragCucumberSingletons.mapJsonSomMap(json)
            jsonMap["datoMottatt"] = LocalDate.now().minusDays(1)

            CucumberTestRun.hentKonfigurertNaisApp(appForDokarkiv).exchangePost(
                endpointUrl = "/rest/journalpostapi/v1/journalpost",
                body = BidragCucumberSingletons.mapTilJsonString(jsonMap),
                customHeaders = arrayOf(NAV_CALL_ID to ScenarioManager.fetchCorrelationIdForScenario())
            )

            val testData = CucumberTestRun.thisRun().testData
            val restTjeneste = CucumberTestRun.hentRestTjenste("dokarkiv-api")
            val jpIdJoark = jacksonObjectMapper().readTree(restTjeneste.hentResponse()).get("journalpostId")
            testData.nye(nokkel, Data(journalpostId = "JOARK-${jpIdJoark.asText()}", joarkJournalpostId = jpIdJoark.asText()))
            testData.nokkel = nokkel
        }

    }

    fun opprettUtgaaendeJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer: String, fagomrade: String) {
        val appForDokarkiv = "dokarkiv-api"
        LOGGER.info("Oppretter journalpost med $appForDokarkiv")

        CucumberTestRun.hentKonfigurertNaisApp(appForDokarkiv).exchangePost(
            endpointUrl = "/rest/journalpostapi/v1/journalpost?forsoekFerdigstill=true",
            body = """
            {
              "datoMottatt": "${LocalDate.now().minusDays(1)}",
              "tittel": "Bidrag automatisk test av distribusjon",
              "journalposttype": "UTGAAENDE",
              "tema": "$fagomrade",
              "behandlingstema": "ab0322",
              "kanal": "NAV_NO",
              "journalfoerendeEnhet": "0701",
              "avsenderMottaker": {
                "id": "15277049616",
                "idType": "FNR",
                "navn": "Blund, Jon"
              },
              "sak": {
                "fagsakId": "$saksnummer",
                "sakstype": "FAGSAK",
                "fagsaksystem": "BISYS"
              },
              "bruker": {
                "id": "15277049616",
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
    }
}
