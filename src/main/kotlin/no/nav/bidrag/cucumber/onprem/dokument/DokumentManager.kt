package no.nav.bidrag.cucumber.onprem.dokument

import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperManager
import org.assertj.core.api.Assertions.assertThat
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

object DokumentManager {
    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(DokumentManager::class.java)

    fun opprettJournalfortJournalpostNarDenIkkeFinnesFraFor(saksnummer: String, fagomrade: String) {
        val appForBidDokJournalpost = "bidrag-dokument-journalpost"
        LOGGER.info("Eksisterer journalpost for saksnummer $saksnummer i $appForBidDokJournalpost?")
        val midlertidigBrevlager = CucumberTestRun.settOppNaisApp(appForBidDokJournalpost)

        midlertidigBrevlager.exchangeGet(
            endpointUrl = "/sak/$saksnummer/journal?fagomrade=$fagomrade",
            failOnNotFound = false
        )

        if (midlertidigBrevlager.hentResponseSomListe().isEmpty()) {
            val appForTestdata = "bidrag-testdata"
            CucumberTestRun.settOppNaisApp(appForTestdata)
            LOGGER.info("Oppretter journalpost med $appForTestdata")

            opprettBidragssak(appForTestdata = appForTestdata, saksnummer = saksnummer)
            opprettJournalpostForSak(appForTestdata = appForTestdata, saksnummer = saksnummer)
        } else {
            LOGGER.info("Fant ${midlertidigBrevlager.hentResponseSomListe().size} journalpost(er) i $appForBidDokJournalpost")
        }
    }

    @Suppress("SameParameterValue")
    private fun opprettBidragssak(appForTestdata: String, saksnummer: String) {
        CucumberTestRun.hentRestTjenste(appForTestdata).exchangePost(
            endpointUrl = "/sak/$saksnummer",
            body = """
            {
              "saksnummer": "$saksnummer",
              "enhetsnummer": "4833"
            }
            """.trimIndent()
        )
    }

    @Suppress("SameParameterValue")
    private fun opprettJournalpostForSak(
        appForTestdata: String,
        saksnummer: String
    ) {
        val testdataApp = CucumberTestRun.hentRestTjenste(appForTestdata)

        testdataApp.exchangePost(
            endpointUrl = "/journalpost",
            body = """
                        {
                        "avsenderNavn": "Cucumber Test",
                        "beskrivelse": "bidrag-cucumber-onprem",
                        "dokumentType": "I",
                        "dokumentdato": "2019-01-01",
                        "dokumentreferanse": "1234567890",
                        "fagomrade": "BID",
                        "journalstatus": "J",
                        "gjelder": "29118012345",
                        "journaldato": "2019-01-01",
                        "mottattDato": "2019-01-01",
                        "skannetDato": "2019-01-01",
                        "saksnummer": "$saksnummer"
                        }
                    """.trimIndent(),
            customHeaders = arrayOf(HttpHeaders.CONTENT_TYPE to MediaType.APPLICATION_JSON_VALUE)
        )

        FellesEgenskaperManager.assertWhenNotSanityCheck(
            FellesEgenskaperManager.Assertion(
                message = "Response fra opprettet testdata",
                value = testdataApp.hentHttpStatus(),
                expectation = HttpStatus.CREATED
            ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
        )
    }

    fun sjekkAtJournalposterSomHentesErBadeFraArkivOgBidragDokumentJournalpost(fagomrade: String, saksnummer: String) {
        val bidragDokument = CucumberTestRun.hentRestTjenesteTilTesting()

        bidragDokument.exchangeGet("/sak/$saksnummer/journal?fagomrade=$fagomrade")

        FellesEgenskaperManager.assertWhenNotSanityCheck(
            FellesEgenskaperManager.Assertion(
                message = "Minst 2 journalposter",
                value = bidragDokument.hentResponseSomListe(),
                expectation = 1
            ) { assertThat(it.value as List<*>).`as`(it.message).hasSizeGreaterThan(it.expectation as Int) }
        )

        FellesEgenskaperManager.assertWhenNotSanityCheck(
            FellesEgenskaperManager.Assertion(
                message = "En journalpost hentet fra arkiv",
                value = bidragDokument.hentResponse(),
                expectation = """"journalpostId":"JOARK-"""
            ) { assertThat(it.value as String).`as`(it.message).contains(it.expectation as String) }
        )

        FellesEgenskaperManager.assertWhenNotSanityCheck(
            FellesEgenskaperManager.Assertion(
                message = "En journalpost hentet fra bidrag-dokument-journalpost",
                value = bidragDokument.hentResponse(),
                expectation = """"journalpostId":"BID-"""
            ) { assertThat(it.value as String).`as`(it.message).contains(it.expectation as String) }
        )
    }
}
