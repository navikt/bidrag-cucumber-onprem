package no.nav.bidrag.cucumber.model

import com.fasterxml.jackson.databind.ObjectMapper
import io.cucumber.java8.Scenario
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.SpringConfig
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * Singletons som er gyldige i en cucumber-kjøring og som er felles for ALLE egenskaper definert i feature-filer
 */
internal object BidragCucumberSingletons {
    private var applicationContext: ApplicationContext? = null
    private var exceptionLogger: ExceptionLogger? = null
    private var objectMapper: ObjectMapper? = null

    fun hentPrototypeFraApplicationContext() = applicationContext?.getBean(HttpHeaderRestTemplate::class.java) ?: doManualInit()
    fun hentFraContext(kClass: KClass<*>) = applicationContext?.getBean(kClass.java)

    private fun doManualInit(): HttpHeaderRestTemplate {
        val httpComponentsClientHttpRequestFactory = SpringConfig().httpComponentsClientHttpRequestFactorySomIgnorererHttps()
        return HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory)
    }

    fun addRunStats(scenario: Scenario) = CucumberTestRun.addToRunStats(scenario)
    fun toJson(body: Any): String = objectMapper?.writeValueAsString(body) ?: """{ "noMappingAvailable":"$body" }"""

    fun scenarioMessage(scenario: Scenario): String {
        val haveScenario = scenario.name != null && scenario.name.isNotBlank()
        return if (haveScenario) "'${scenario.name}'" else "scenario in ${scenario.uri}"
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
}
