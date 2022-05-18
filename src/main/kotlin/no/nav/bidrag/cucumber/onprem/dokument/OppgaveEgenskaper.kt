package no.nav.bidrag.cucumber.onprem.dokument

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenste
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.settOppNaisApp
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.thisRun
import no.nav.bidrag.cucumber.model.RestTjeneste
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperManager
import org.assertj.core.api.Assertions
import org.awaitility.kotlin.await
import org.springframework.http.HttpStatus
import java.util.concurrent.TimeUnit

@Suppress("unused") // used by cucumber
class OppgaveEgenskaper : No {

    init {
        fun hentOppgaverMedType(oppgaveType: String): List<Map<String, String>> {
            val response = hentRestTjenste("oppgave-api").hentResponseSomMap()
            val oppgaver: List<Map<String, String>> = response["oppgaver"] as List<Map<String, String>>
            return oppgaver.filter { it["oppgavetype"] == oppgaveType }
        }

        fun validerHarAntallOppgaverMedType(antall: Int, oppgaveType: String) {
            val jfrOppgaver = hentOppgaverMedType(oppgaveType)
            Assertions.assertThat(jfrOppgaver.size).isEqualTo(antall)
        }

        fun hentAapneOppgaver() {
            val oppgave = settOppNaisApp("oppgave-api")
            val testdata = thisRun().testData
            val journalpostIdUtenPrefix = testdata.hentJournalpostIdUtenPrefix(testdata.nokkel)
            oppgave.exchangeGet("/api/v1/oppgaver/?journalpostId=JOARK-$journalpostIdUtenPrefix&journalpostId=BID-$journalpostIdUtenPrefix&journalpostId=$journalpostIdUtenPrefix&statuskategori=AAPEN")
        }


        Gitt("alle søknadsoppgaver med saksnummer {string} er lukket") { saksnummer: String ->
            val oppgave = settOppNaisApp("oppgave-api")
            oppgave.exchangeGet("/api/v1/oppgaver/?saksreferanse=$saksnummer&statuskategori=AAPEN")
            val oppgaver = hentOppgaverMedType("BEH_SAK")
            oppgaver.forEach {
                val oppgaveId = it["id"].toString()
                val versjon = it["versjon"].toString()
                oppgave.exchangePatch("/api/v1/oppgaver/${oppgaveId}",
                    "{\n" +
                        "             \"id\": ${oppgaveId},\n" +
                        "             \"versjon\": ${versjon},\n" +
                        "             \"status\": \"FERDIGSTILT\"\n" +
                        "        }"
                )

            }
        }

        Gitt("jeg søker etter oppgaver for mottaksregistrert journalpost") {
            hentAapneOppgaver()
        }

        Så("skal http status for oppgavesøket være {int}") { httpStatus: Int ->
            val oppgave = hentRestTjenste("oppgave-api")
            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "HttpStatus for ${oppgave.hentFullUrlMedEventuellWarning()}",
                    value = CucumberTestRun.hentRestTjenesteTilTesting().hentHttpStatus(),
                    expectation = HttpStatus.valueOf(httpStatus)
                ) { Assertions.assertThat(it.value).`as`(it.message).isEqualTo(it.expectation) }
            )
        }

        Og("skal ha totalt {int} åpne søknadsoppgaver") { antall: Int ->
            await.atMost(15, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                .pollInSameThread()
                .untilAsserted {
                    hentAapneOppgaver()
                    validerHarAntallOppgaverMedType(antall, "BEH_SAK")
                }
        }

        Og("skal ha totalt {int} åpne journalføringsoppgaver") { antall: Int ->
            await.atMost(15, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS)
                .pollInSameThread()
                .untilAsserted {
                    hentAapneOppgaver()
                    validerHarAntallOppgaverMedType(antall, "JFR")
                }
        }

        Og("skal responsen fra oppgave med type {string} inneholde feltet {string} = {string}") { type: String, key: String, value: String ->
            val oppgave = hentOppgaverMedType(type)[0]
            Assertions.assertThat(oppgave[key]).isEqualTo(value)
        }

        Og("søkeresultatet skal inneholde {int} oppgave") { antall: Int ->
            val response = hentRestTjenste("oppgave-api").hentResponse()

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