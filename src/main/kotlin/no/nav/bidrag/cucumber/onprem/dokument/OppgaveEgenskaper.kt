package no.nav.bidrag.cucumber.onprem.dokument

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperManager
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.springframework.http.HttpStatus
import java.util.concurrent.TimeUnit

@Suppress("unused") // used by cucumber
class OppgaveEgenskaper: No {

    init {

        Gitt("jeg søker etter oppgaver for mottaksregistrert journalpost") {
            Thread.sleep(2000)
            val oppgave = CucumberTestRun.settOppNaisApp("oppgave-api")
            val testdata = CucumberTestRun.thisRun().testData
            val journalpostIdUtenPrefix = testdata.hentJournalpostIdUtenPrefix(testdata.nokkel)

            oppgave.exchangeGet("/api/v1/oppgaver/?journalpostId=JOARK-$journalpostIdUtenPrefix&journalpostId=BID-$journalpostIdUtenPrefix&journalpostId=$journalpostIdUtenPrefix&statuskategori=AAPEN")
        }

        Så("skal http status for oppgavesøket være {int}") { httpStatus: Int ->
            val oppgave = CucumberTestRun.hentRestTjenste("oppgave-api")
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "HttpStatus for ${oppgave.hentFullUrlMedEventuellWarning()}",
                    value = CucumberTestRun.hentRestTjenesteTilTesting().hentHttpStatus(),
                    expectation = HttpStatus.valueOf(httpStatus)
                ) { Assertions.assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("skal ha totalt {int} åpne journalføringsoppgaver") { antall: Int ->
            val oppgave = CucumberTestRun.settOppNaisApp("oppgave-api")
            val testdata = CucumberTestRun.thisRun().testData
            val journalpostIdUtenPrefix = testdata.hentJournalpostIdUtenPrefix(testdata.nokkel)

            await.atMost(15, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).untilAsserted {
                val response =
                    oppgave.exchangeGet("/api/v1/oppgaver/?journalpostId=JOARK-$journalpostIdUtenPrefix&journalpostId=BID-$journalpostIdUtenPrefix&journalpostId=$journalpostIdUtenPrefix&statuskategori=AAPEN&oppgavetype=JFR&sorteringsrekkefolge=ASC&sorteringsfelt=FRIST&limit=100")

                FellesEgenskaperManager.assertWhenNotSanityCheck(
                    Assertion(
                        message = "søkeresultatet skal inneholde en oppgave",
                        value = response.body,
                        expectation = """antallTreffTotalt":$antall"""
                    ) { Assertions.assertThat(it.value as String?).`as`(it.message).contains(it.expectation as String) }
                )
            }
        }

        Og("skal responsen fra oppgave inneholde feltet {string} = {string}") { key: String, value: String ->
            val response = CucumberTestRun.hentRestTjenste("oppgave-api").hentResponse()
            val oppgaver = jacksonObjectMapper().readTree(response).get("oppgaver")
            Assertions.assertThat(oppgaver.size()).isEqualTo(1)
            val oppgave = oppgaver.get(0)
            Assertions.assertThat(oppgave.get(key).textValue()).isEqualTo(value)
        }

        Og("søkeresultatet skal inneholde {int} oppgave") { antall: Int ->
            val response = CucumberTestRun.hentRestTjenste("oppgave-api").hentResponse()

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "søkeresultatet skal inneholde en oppgave",
                    value = response,
                    expectation = """antallTreffTotalt":$antall"""
                ) { Assertions.assertThat(it.value as String?).`as`(it.message).contains(it.expectation as String) }
            )
        }
    }
}