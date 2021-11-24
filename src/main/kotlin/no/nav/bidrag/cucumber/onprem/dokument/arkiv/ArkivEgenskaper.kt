package no.nav.bidrag.cucumber.onprem.dokument.arkiv

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.RestTjeneste
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService
import org.assertj.core.api.Assertions.assertThat

@Suppress("unused") // used by cucumber
class ArkivEgenskaper : No {
    private lateinit var fagomrade: String
    private lateinit var saksnummer: String

    init {
        Og("det finnes en journalpost for saksnummer {string} på fagområdet {string}") { saksnummer: String, fagomrade: String ->
            this.fagomrade = fagomrade
            this.saksnummer = saksnummer
            ArkivManager.opprettJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer, fagomrade)
        }

        Når("jeg kaller endpoint {string} med saksnummer på fagområde") { endpoint: String ->
            RestTjeneste(naisApplication = "bidrag-dokument-arkiv").exchangeGet(
                "${endpoint.replace("{saksnummer}", saksnummer)}?fagomrade=$fagomrade"
            )
        }

        Og("så skal responsen inneholde et objekt med feltet for saksnummer") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                FellesEgenskaperService.Assertion(
                    message = "En json liste som har objekt med saksnummer",
                    value = RestTjeneste(naisApplication = "bidrag-dokument-arkiv").hentResponse(),
                    expectation = """"saksnummer":"$saksnummer""""
                ) { assertThat(it.value as String?).`as`(it.message).startsWith("[{").contains(it.expectation as String) }
            )
        }

        Så("så skal responsen være ei tom liste") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                FellesEgenskaperService.Assertion(
                    message = "Respons fra ${CucumberTestRun.hentRestTjenesteTilTesting().hentFullUrlMedEventuellWarning()}",
                    value = CucumberTestRun.hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "[]",
                ) { assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("så skal responsen være ei liste som ikke er tom") {
            FellesEgenskaperService.assertWhenNotSanityCheck(
                FellesEgenskaperService.Assertion(
                    message = "Response er ei liste i json som ikke er tom",
                    value = CucumberTestRun.hentRestTjenesteTilTesting().hentResponse()?.trim(),
                    expectation = "["
                ) { assertThat(it.value as String).`as`(it.message).startsWith(it.expectation as String).hasSizeGreaterThan(2) }
            )
        }
    }
}
