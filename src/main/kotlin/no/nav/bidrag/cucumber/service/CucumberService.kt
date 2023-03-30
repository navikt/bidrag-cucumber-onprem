package no.nav.bidrag.cucumber.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.core.cli.Main
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.cucumber.ABSOLUTE_FEATURE_PATH
import no.nav.bidrag.cucumber.model.BidragCucumberSingletons
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.SuppressStackTraceText
import no.nav.bidrag.cucumber.model.TestFailedException
import no.nav.bidrag.cucumber.onprem.TestDataManager
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class CucumberService(
    private val suppressStackTraceText: SuppressStackTraceText,
    applicationContext: ApplicationContext,
    exceptionLogger: ExceptionLogger,
    objectMapper: ObjectMapper
) {
    init {
        BidragCucumberSingletons.setApplicationContext(applicationContext)
        BidragCucumberSingletons.setExceptionLogger(exceptionLogger)
        BidragCucumberSingletons.setObjectMapper(objectMapper)
    }

    internal fun run(cucumberTestRun: CucumberTestRun): String {
        val result = runCucumberTests(cucumberTestRun.tags)

        val suppressedStackTraceLog = suppressStackTraceText.suppress(
            CucumberTestRun.fetchTestMessagesWithRunStats()
        )

        if (CucumberTestRun.isTestDataPresent()) {
            TestDataManager.slettTestData()
        }

        CucumberTestRun.endRun()

        if (result != 0.toByte()) {
            throw TestFailedException("Cucumber tests failed! (tags: ${cucumberTestRun.tags})!", suppressedStackTraceLog)
        }

        return suppressedStackTraceLog
    }

    private fun runCucumberTests(tags: String): Byte {
        if (tags.isBlank()) throw IllegalStateException("Ingen tags som kan brukes")

        return Main.run(
            ABSOLUTE_FEATURE_PATH,
            "--glue",
            "no.nav.bidrag.cucumber.onprem",
            "--tags",
            tags
        )
    }
}
