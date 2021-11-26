package no.nav.bidrag.cucumber.model

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.SpringConfig
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.reflect.KClass

/**
 * Singletons som er gyldige i en cucumber-kjøring og som er felles for ALLE egenskaper definert i feature-filer
 */
internal object BidragCucumberSingletons {
    private var applicationContext: ApplicationContext? = null
    private var exceptionLogger: ExceptionLogger? = null
    private var objectMapper: ObjectMapper? = null

    @Suppress("UNCHECKED_CAST")
    fun <T> hentFraContext(kClass: KClass<*>): T = applicationContext?.getBean(kClass.java) as T
    fun hentPrototypeFraApplicationContext() = applicationContext?.getBean(HttpHeaderRestTemplate::class.java) ?: doManualInit()
    private fun fetchObjectMapper() = objectMapper ?: ObjectMapper()

    private fun doManualInit(): HttpHeaderRestTemplate {
        val httpComponentsClientHttpRequestFactory = SpringConfig().httpComponentsClientHttpRequestFactorySomIgnorererHttps()
        return HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory)
    }

    fun mapResponseSomMap(responseEntity: ResponseEntity<String?>?): Map<String, Any> {
        return if (responseEntity?.statusCode == HttpStatus.OK && responseEntity.body != null)
            mapResponseSomMap(responseEntity.body!!)
        else
            HashMap()
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapResponseSomMap(body: String): Map<String, Any> = try {
        fetchObjectMapper().readValue(body, Map::class.java) as Map<String, Any>
    } catch (e: Exception) {
        CucumberTestRun.holdExceptionForTest(e)
        throw e
    }

    fun mapResponseSomListe(responseEntity: ResponseEntity<String?>?): List<Any> {
        return if (responseEntity?.statusCode == HttpStatus.OK && responseEntity.body != null)
            mapResponseSomListe(responseEntity.body!!)
        else
            ArrayList()
    }

    private fun mapResponseSomListe(body: String): List<Any> = try {
        fetchObjectMapper().readValue(body, ArrayList::class.java)
    } catch (e: Exception) {
        CucumberTestRun.holdExceptionForTest(e)
        throw e
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
