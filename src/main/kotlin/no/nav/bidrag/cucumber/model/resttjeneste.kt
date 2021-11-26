package no.nav.bidrag.cucumber.model

import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.web.EnhetFilter
import no.nav.bidrag.cucumber.Headers
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.service.AzureTokenService
import no.nav.bidrag.cucumber.service.OidcTokenService
import no.nav.bidrag.cucumber.service.StsService
import no.nav.bidrag.cucumber.service.TokenService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

internal class RestTjenester {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(CucumberTestRun::class.java)

        internal fun konfigurerApplikasjonUrlFor(applicationName: String): String {
            val ingress = CucumberTestRun.fetchIngress(applicationName)

            if (CucumberTestRun.isNoContextPathForApp(applicationName)) {
                return ingress
            }

            val ingressUrl = if (ingress.endsWith('/')) ingress.removeSuffix("/") else ingress

            return "$ingressUrl/$applicationName"
        }
    }

    private val restTjenesteForNavn: MutableMap<String, RestTjeneste> = HashMap()
    private var restTjenesteTilTesting: RestTjeneste? = null

    fun settOppNaisApp(naisApplikasjon: String): RestTjeneste {
        LOGGER.info("Setter opp $naisApplikasjon")

        val restTjeneste: RestTjeneste

        if (!restTjenesteForNavn.contains(naisApplikasjon)) {
            restTjeneste = RestTjeneste.konfigurerResttjeneste(naisApplikasjon)
            restTjenesteForNavn[naisApplikasjon] = restTjeneste
        } else {
            restTjeneste = restTjenesteForNavn[naisApplikasjon]!!
        }

        return restTjeneste
    }

    fun settOppNaisAppTilTesting(naisApplikasjon: String) {
        restTjenesteTilTesting = settOppNaisApp(naisApplikasjon)
    }

    fun isApplicationConfigured(applicationName: String) = restTjenesteForNavn.contains(applicationName)
    fun hentRestTjenesteTilTesting() = restTjenesteTilTesting ?: throw IllegalStateException("RestTjeneste til testing er null!")
    fun hentRestTjeneste(applicationName: String) = restTjenesteForNavn[applicationName] ?: throw IllegalStateException(
        "RestTjeneste $applicationName er ikke funnet!"
    )
}

internal class BaseUrlTemplateHandler(private val baseUrl: String) : UriTemplateHandler {
    override fun expand(uriTemplate: String, uriVariables: MutableMap<String, *>): URI {
        if (uriVariables.isNotEmpty()) {
            val queryString = StringBuilder()
            uriVariables.forEach { if (queryString.length == 1) queryString.append("$it") else queryString.append("?$it") }

            return URI.create(baseUrl + uriTemplate + queryString)
        }

        return URI.create(baseUrl + uriTemplate)
    }

    override fun expand(uriTemplate: String, vararg uriVariables: Any?): URI {
        if (uriVariables.isNotEmpty() && (uriVariables.size != 1 && uriVariables.first() != null)) {
            val queryString = StringBuilder("&")
            uriVariables.forEach {
                if (it != null && queryString.length == 1) {
                    queryString.append("$it")
                } else if (it != null) {
                    queryString.append("?$it")
                }
            }

            return URI.create(baseUrl + uriTemplate + queryString)
        }

        return URI.create(baseUrl + uriTemplate)
    }
}

class RestTjeneste(
    internal val rest: ResttjenesteMedBaseUrl
) {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(RestTjeneste::class.java)

        internal fun konfigurerResttjeneste(applicationName: String): RestTjeneste {
            if (CucumberTestRun.isApplicationConfigured(applicationName)) {
                return CucumberTestRun.hentRestTjenste(applicationName)
            }

            val applicationUrl = RestTjenester.konfigurerApplikasjonUrlFor(applicationName)
            val httpHeaderRestTemplate = BidragCucumberSingletons.hentPrototypeFraApplicationContext()
            httpHeaderRestTemplate.uriTemplateHandler = BaseUrlTemplateHandler(applicationUrl)

            if (CucumberTestRun.isTestUserPresent) {
                val tokenValue = hentSaksbehandlerToken(applicationName)
                httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { tokenValue.initBearerToken() }
            } else {
                LOGGER.info("No user to provide security for when accessing $applicationName")
            }

            if (StsService.supportedApplications.contains(applicationName)) {
                val stsTokenValue = hentStsToken()
                httpHeaderRestTemplate.addHeaderGenerator(Headers.NAV_CONSUMER_TOKEN) { stsTokenValue.initBearerToken() }
            }

            return RestTjeneste(ResttjenesteMedBaseUrl(httpHeaderRestTemplate, applicationUrl))
        }

        private fun hentSaksbehandlerToken(applicationName: String): TokenValue {
            val tokenService: TokenService = when (CucumberTestRun.hentTokenType()) {
                TokenType.AZURE -> BidragCucumberSingletons.hentFraContext(AzureTokenService::class) ?: throw notNullTokenService(TokenType.AZURE)
                TokenType.OIDC -> BidragCucumberSingletons.hentFraContext(OidcTokenService::class) ?: throw notNullTokenService(TokenType.OIDC)
            }

            return TokenValue(tokenService.cacheGeneratedToken(applicationName))
        }

        private fun hentStsToken(): TokenValue {
            val stsService: StsService = BidragCucumberSingletons.hentFraContext(StsService::class)
            return TokenValue(stsService.hentServiceBrukerOidcToken() ?: throw IllegalStateException("Token er null!"))
        }

        private fun notNullTokenService(tokenType: TokenType) = IllegalStateException("No service for $tokenType in spring context")
    }

    private lateinit var fullUrl: FullUrl
    private var responseEntity: ResponseEntity<String?>? = null

    fun hentFullUrlMedEventuellWarning() = "$fullUrl${appendWarningWhenExists()}"
    fun hentHttpHeaders(): HttpHeaders = responseEntity?.headers ?: HttpHeaders()
    fun hentHttpStatus(): HttpStatus = responseEntity?.statusCode ?: HttpStatus.I_AM_A_TEAPOT
    fun hentResponse(): String? = responseEntity?.body
    fun hentResponseSomListe(): List<Any> = BidragCucumberSingletons.mapResponseSomListe(responseEntity)
    fun hentResponseSomMap() = BidragCucumberSingletons.mapResponseSomMap(responseEntity)

    private fun appendWarningWhenExists(): String {
        val warnings = responseEntity?.headers?.get(HttpHeaders.WARNING) ?: emptyList()

        return if (warnings.isNotEmpty()) " - ${warnings[0]}" else ""
    }

    fun exchangeGet(endpointUrl: String): ResponseEntity<String?> {
        fullUrl = FullUrl(rest.baseUrl, endpointUrl)

        val header = initHttpHeadersWithCorrelationIdEnhetAnd()

        exchange(HttpEntity(null, header), endpointUrl, HttpMethod.GET)

        LOGGER.info(
            if (responseEntity?.body != null) "response with body and status ${responseEntity!!.statusCode}"
            else if (responseEntity == null) "no response entity (${sanityCheck()})" else "no response body with status ${responseEntity!!.statusCode}"
        )

        return responseEntity ?: ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build()
    }

    private fun sanityCheck(): String {
        return if (CucumberTestRun.isSanityCheck) "is sanity check" else "is NOT sanity check"
    }

    private fun initHttpHeadersWithCorrelationIdEnhetAnd(customHeaders: Array<out Pair<String, String>> = emptyArray()): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(CorrelationId.CORRELATION_ID_HEADER, ScenarioManager.fetchCorrelationIdForScenario())
        headers.add(EnhetFilter.X_ENHET_HEADER, "4802")

        customHeaders.forEach { headers.add(it.first, it.second) }

        return headers
    }

    fun exchangePost(endpointUrl: String, body: String, vararg customHeaders: Pair<String, String>) {
        val jsonEntity = httpEntity(endpointUrl, body, customHeaders)
        exchange(jsonEntity, endpointUrl, HttpMethod.POST)
    }

    private fun httpEntity(endpointUrl: String, body: Any, customHeaders: Array<out Pair<String, String>>): HttpEntity<*> {
        this.fullUrl = FullUrl(rest.baseUrl, endpointUrl)
        val headers = initHttpHeadersWithCorrelationIdEnhetAnd(customHeaders)
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(body, headers)
    }

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

            if (CucumberTestRun.isNotSanityCheck) {
                throw e
            }
        }
    }
}

class ResttjenesteMedBaseUrl(val template: RestTemplate, val baseUrl: String)
class TokenValue(private val token: String) {
    fun initBearerToken() = "Bearer $token"
}

internal class FullUrl(baseUrl: String, endpointUrl: String) {
    private val fullUrl: String = "$baseUrl$endpointUrl"

    override fun toString(): String {
        return fullUrl
    }
}
