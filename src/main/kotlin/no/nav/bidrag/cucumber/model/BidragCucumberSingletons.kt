package no.nav.bidrag.cucumber.model

import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.java8.Scenario
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.SpringConfig
import no.nav.bidrag.cucumber.service.AzureTokenService
import no.nav.bidrag.cucumber.service.OidcTokenService
import no.nav.bidrag.cucumber.sikkerhet.TokenService
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * Singletons som er gyldige i en cucumber-kjøring og som er felles for ALLE egenskaper definert i feature-filer
 */
internal object BidragCucumberSingletons {
    @JvmStatic
    private val RUN_STATS = ThreadLocal<RunStats>()

    private var applicationContext: ApplicationContext? = null
    private var exceptionLogger: ExceptionLogger? = null
    private var objectMapper: ObjectMapper? = null
    private var testMessagesHolder: TestMessagesHolder? = null

    fun hentPrototypeFraApplicationContext() = applicationContext?.getBean(HttpHeaderRestTemplate::class.java) ?: doManualInit()
    fun hentFraContext(kClass: KClass<*>) = applicationContext?.getBean(kClass.java)

    private fun doManualInit(): HttpHeaderRestTemplate {
        val httpComponentsClientHttpRequestFactory = SpringConfig().httpComponentsClientHttpRequestFactorySomIgnorererHttps()
        return HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory)
    }

    fun holdTestMessage(testMessage: String) {
        testMessagesHolder?.hold(testMessage)
    }

    fun addRunStats(scenario: Scenario) = fetchRunStats()
        .add(scenario)

    fun scenarioMessage(scenario: Scenario): String {
        val haveScenario = scenario.name != null && scenario.name.isNotBlank()
        return if (haveScenario) "'${scenario.name}'" else "scenario in ${scenario.uri}"
    }

    fun fetchTestMessagesWithRunStats() = fetchTestMessages() + "\n\n" + fetchRunStats()

    private fun fetchTestMessages() = testMessagesHolder?.fetchTestMessages() ?: "ingen loggmeldinger er produsert!"

    private fun fetchRunStats(): RunStats {
        var runStats = RUN_STATS.get()

        if (runStats == null) {
            runStats = RunStats()
            RUN_STATS.set(runStats)
        }

        return runStats
    }

    fun removeRunStats() {
        RUN_STATS.remove()
    }

    fun holdExceptionForTest(throwable: Throwable) {
        val assertionMessage = "${throwable.javaClass.simpleName}: ${throwable.message}"

        testMessagesHolder?.hold(assertionMessage)
        fetchRunStats().addExceptionLogging(listOf(assertionMessage))
    }

    fun setApplicationContext(applicationContext: ApplicationContext) {
        BidragCucumberSingletons.applicationContext = applicationContext
    }

    fun setExceptionLogger(exceptionLogger: ExceptionLogger) {
        BidragCucumberSingletons.exceptionLogger = exceptionLogger
    }

    fun setObjectMapper(objectMapper: ObjectMapper) {
        BidragCucumberSingletons.objectMapper = objectMapper
    }

    fun setTestMessagesHolder(testMessagesHolder: TestMessagesHolder) {
        BidragCucumberSingletons.testMessagesHolder = testMessagesHolder
    }
}
