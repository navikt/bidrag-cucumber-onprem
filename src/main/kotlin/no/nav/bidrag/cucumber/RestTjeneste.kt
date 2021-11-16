package no.nav.bidrag.cucumber

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.web.EnhetFilter.X_ENHET_HEADER
import no.nav.bidrag.cucumber.model.BidragCucumberSingletons
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate

@Suppress("UNCHECKED_CAST")
open class RestTjeneste(
    internal val rest: ResttjenesteMedBaseUrl
) {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(Environment::class.java)
    }

    private lateinit var fullUrl: FullUrl
    private var responseEntity: ResponseEntity<String?>? = null

    constructor(naisApplication: String) : this(RestTjenesteForApplikasjon.hentEllerKonfigurer(naisApplication))

    fun hentFullUrlMedEventuellWarning() = "$fullUrl${appendWarningWhenExists()}"
    fun hentHttpStatus(): HttpStatus = responseEntity?.statusCode ?: HttpStatus.I_AM_A_TEAPOT
    fun hentResponse(): String? = responseEntity?.body
    fun hentResponseSomMap(): Map<String, Any> = if (responseEntity?.statusCode == HttpStatus.OK && responseEntity?.body != null)
        mapResponseBody(responseEntity?.body!!)
    else
        HashMap()

    private fun mapResponseBody(body: String): Map<String, Any> = try {
        ObjectMapper().readValue(body, Map::class.java) as Map<String, Any>
    } catch (e: Exception) {
        BidragCucumberSingletons.holdExceptionForTest(e)
        throw e
    }

    private fun appendWarningWhenExists(): String {
        val warnings = responseEntity?.headers?.get(HttpHeaders.WARNING) ?: emptyList()

        return if (warnings.isNotEmpty()) " - ${warnings[0]}" else ""
    }

    fun exchangeGet(endpointUrl: String): ResponseEntity<String?> {
        fullUrl = FullUrl(rest.baseUrl, endpointUrl)

        val header = initHttpHeadersWithCorrelationIdAndEnhet()

        exchange(HttpEntity(null, header), endpointUrl, HttpMethod.GET)

        LOGGER.info(
            if (responseEntity?.body != null) "response with body and status ${responseEntity!!.statusCode}"
            else if (responseEntity == null) "no response entity (${sanityCheck()})" else "no response body with status ${responseEntity!!.statusCode}"
        )

        return responseEntity ?: ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build()
    }

    private fun sanityCheck(): String {
        return if (Environment.isSanityCheck) "is sanity check" else "is NOT sanity check"
    }

    private fun initHttpHeadersWithCorrelationIdAndEnhet(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(CorrelationId.CORRELATION_ID_HEADER, ScenarioManager.getCorrelationIdForScenario())
        headers.add(X_ENHET_HEADER, "4802")

        return headers
    }

    fun exchangePost(endpointUrl: String, json: String) {
        val jsonEntity = httpEntity(endpointUrl, json)
        exchange(jsonEntity, endpointUrl, HttpMethod.POST)
    }

    private fun httpEntity(endpointUrl: String, body: Any): HttpEntity<*> {
        this.fullUrl = FullUrl(rest.baseUrl, endpointUrl)
        val headers = initHttpHeadersWithCorrelationIdAndEnhet()
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(body, headers)
    }

    fun hentHttpHeaders(): HttpHeaders = responseEntity?.headers ?: HttpHeaders()

    private fun exchange(jsonEntity: HttpEntity<*>, endpointUrl: String, httpMethod: HttpMethod) {
        LOGGER.info("$httpMethod: $fullUrl")

        try {
            responseEntity = rest.template.exchange(endpointUrl, httpMethod, jsonEntity, String::class.java)
        } catch (e: Exception) {
            ScenarioManager.errorLog("$httpMethod FEILET! ($fullUrl)", e)

            responseEntity = if (e is HttpStatusCodeException) {
                ResponseEntity.status(e.statusCode).body<String>("${e.javaClass.simpleName}: ${e.message}")
            } else {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<String>("${e.javaClass.simpleName}: ${e.message}")
            }

            if (Environment.isNotSanityCheck) {
                throw e
            }
        }
    }

    class ResttjenesteMedBaseUrl(val template: RestTemplate, val baseUrl: String)
    internal class FullUrl(baseUrl: String, endpointUrl: String) {
        private val fullUrl: String = "$baseUrl$endpointUrl"

        override fun toString(): String {
            return fullUrl
        }
    }
}
