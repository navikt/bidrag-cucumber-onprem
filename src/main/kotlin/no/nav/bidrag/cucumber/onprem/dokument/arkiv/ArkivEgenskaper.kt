package no.nav.bidrag.cucumber.onprem.dokument.arkiv

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperManager
import no.nav.bidrag.cucumber.onprem.dokument.DokumentManager
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus

@Suppress("unused") // used by cucumber
class ArkivEgenskaper : No {
    private lateinit var fagomrade: String
    private lateinit var saksnummer: String

    init {
        Og("det finnes en ferdigstilt journalpost for saksnummer {string} på fagområdet {string}") { saksnummer: String, fagomrade: String ->
            this.fagomrade = fagomrade
            this.saksnummer = saksnummer
            DokumentManager.opprettBidragssak("bidrag-testdata", saksnummer)
            ArkivManager.opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer, fagomrade)
        }

        Når("jeg kaller endpoint {string} med saksnummer på fagområde") { endpoint: String ->
            hentRestTjenesteTilTesting().exchangeGet(
                "${endpoint.replace("{saksnummer}", saksnummer)}?fagomrade=$fagomrade"
            )
        }

        Og("så skal responsen inneholde en journalført journalpost") {
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "En json liste med en journalført journalpost",
                    value = hentRestTjenesteTilTesting().hentResponse(),
                    expectation = """"journalstatus":"J""""
                ) { assertThat(it.value as String?).`as`(it.message).startsWith("[{").contains(it.expectation as String) }
            )
        }

        Så("så skal responsen være ei tom liste") {
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Respons fra ${hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}",
                    value = hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "[]",
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("så skal responsen være ei liste som ikke er tom") {
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Response er ei liste i json som ikke er tom",
                    value = hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "[{"
                ) { assertThat(it.value as String).`as`(it.message).startsWith(it.expectation as String) }
            )
        }

        Og("så skal responsen inneholde en journalpost med JOARK prefix") {
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Responsen inneholder en journalpost med JOARK-prefix",
                    value = hentRestTjenesteTilTesting().hentResponse(),
                    expectation = """"journalpostId":"JOARK-""",
                ) { assertThat(it.value as String).`as`(it.message).contains(it.expectation as String) }
            )
        }

        Gitt("jeg søker etter oppgaver for mottaksregistrert journalpost") {
            val dokarkiv = CucumberTestRun.settOppNaisApp("dokarkiv-api")
            val testdata = CucumberTestRun.thisRun().testData
            val journalpostIdUtenPrefix = testdata.hentJournalpostIdUtenPrefix(testdata.nokkel)

            dokarkiv.exchangeGet("/rest/journalpostapi/v1?journalpostId=$journalpostIdUtenPrefix&statuskategori=AAPEN")
        }

        Så("skal http status for oppgavesøket være {int}") { httpStatus: Int ->
            val dokarkiv = CucumberTestRun.hentRestTjenste("dokarkiv-api")
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "HttpStatus for ${dokarkiv.hentFullUrlMedEventuellWarning()}",
                    value = hentRestTjenesteTilTesting().hentHttpStatus(),
                    expectation = HttpStatus.valueOf(httpStatus)
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("søkeresultatet skal inneholde en oppgave") {
            val response = CucumberTestRun.hentRestTjenste("dokarkiv-api").hentResponse()

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "søkeresultatet skal inneholde en oppgave",
                    value = response,
                    expectation = """"antallTreffTotalt":1""""
                ) { assertThat(it.value as String?).`as`(it.message).contains(it.expectation as String) }
            )
        }
    }
}
