package no.nav.bidrag.cucumber.onprem.dokument

import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperManager
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.springframework.http.HttpStatus
import java.util.concurrent.TimeUnit

object KafkaManager {
    private const val naisAppReadingKafka = "bidrag-testdata"

    fun sjekkAtJournalpostHendelseErRegistrert(journalpostId: String) {
        val naisAppWithKafka = CucumberTestRun.hentKonfigurertNaisApp(naisAppReadingKafka)
        await.atMost(15, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).untilAsserted {

            val response = naisAppWithKafka.exchangeGet("/hendelser/$journalpostId")

            FellesEgenskaperManager.assertWhenNotSanityCheck(
                Assertion(
                    message = "Forventet å finne journalposthendelse for $journalpostId",
                    value = response.statusCode,
                ) { assertThat(it.value).`as`(it.message).isEqualTo(HttpStatus.OK) }
            )
        }
    }
}