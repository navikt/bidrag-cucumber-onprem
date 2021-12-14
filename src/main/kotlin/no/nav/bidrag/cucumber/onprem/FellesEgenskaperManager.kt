package no.nav.bidrag.cucumber.onprem

import no.nav.bidrag.cucumber.model.Assertion
import no.nav.bidrag.cucumber.model.CucumberTestRun
import org.slf4j.LoggerFactory

object FellesEgenskaperManager {
    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(FellesEgenskaperManager::class.java)

    fun assertWhenNotSanityCheck(assertion: Assertion) {
        LOGGER.info(
            """
            Assertion, actual: '${assertion.value}' ${assertion.value?.javaClass.let { "- ($it)" }},
            used as expecteation: '${assertion.expectation}' ${assertion.expectation?.javaClass.let { "- ($it)" }},
            sanity check: ${CucumberTestRun.isSanityCheck}
            """.trimIndent()
        )

        if (CucumberTestRun.isNotSanityCheck) {
            assertion.doVerify()
        }
    }
}
