package no.nav.bidrag.cucumber.onprem.dokument.arkiv

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService
import org.assertj.core.api.Assertions.assertThat

@Suppress("unused") // used by cucumber
class ArkivEgenskaper : No {
    private lateinit var fagomrade: String
    private lateinit var saksnummer: String

    init {
        Og("det finnes en ferdigstilt journalpost for saksnummer {string} på fagområdet {string}") { saksnummer: String, fagomrade: String ->
            this.fagomrade = fagomrade
            this.saksnummer = saksnummer
            ArkivManager.opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer, fagomrade)
        }

        Når("jeg kaller endpoint {string} med saksnummer på fagområde") { endpoint: String ->
            hentRestTjenesteTilTesting().exchangeGet(
                "${endpoint.replace("{saksnummer}", saksnummer)}?fagomrade=$fagomrade"
            )
        }

        Og("så skal responsen inneholde en journalført journalpost") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                FellesEgenskaperService.Assertion(
                    message = "En json liste med en journalført journalpost",
                    value = hentRestTjenesteTilTesting().hentResponse(),
                    expectation = """"journalstatus":"J""""
                ) { assertThat(it.value as String?).`as`(it.message).startsWith("[{").contains(it.expectation as String) }
            )
        }

        Så("så skal responsen være ei tom liste") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                FellesEgenskaperService.Assertion(
                    message = "Respons fra ${hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}",
                    value = hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "[]",
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("så skal responsen være ei liste som ikke er tom") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                FellesEgenskaperService.Assertion(
                    message = "Response er ei liste i json som ikke er tom",
                    value = hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "[{"
                ) { assertThat(it.value as String).`as`(it.message).startsWith(it.expectation as String) }
            )
        }

        Og("så skal responsen inneholde en journalpost med JOARK prefix") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                FellesEgenskaperService.Assertion(
                    message = "Responsen inneholder en journalpost med JOARK-prefix",
                    value = hentRestTjenesteTilTesting().hentResponse(),
                    expectation = """"journalpostId":"JOARK-""",
                ) { assertThat(it.value as String).`as`(it.message).contains(it.expectation as String) }
            )
        }
    }
}
