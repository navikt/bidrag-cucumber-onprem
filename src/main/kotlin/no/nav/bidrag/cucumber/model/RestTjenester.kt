package no.nav.bidrag.cucumber.model

import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.commons.web.EnhetFilter
import no.nav.bidrag.cucumber.Headers
import no.nav.bidrag.cucumber.ScenarioManager
import no.nav.bidrag.cucumber.service.AzureTokenService
import no.nav.bidrag.cucumber.service.StsService
import no.nav.bidrag.cucumber.service.StsTokenService
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

    fun isApplicationConfigured(applicationName: String) = restTjenesteForNavn.contains(applicationName)
    fun hentRestTjenesteTilTesting() = restTjenesteTilTesting ?: throw IllegalStateException("RestTjeneste til testing er null!")
    fun hentRestTjeneste(applicationName: String) = restTjenesteForNavn[applicationName] ?: throw IllegalStateException(
        "RestTjeneste $applicationName er ikke funnet blant konfigurerte applikasjoner: ${restTjenesteForNavn.keys}!"
    )

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

            try {
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
                    if (!CucumberTestRun.isTestUserPresent) {
                        httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { stsTokenValue.initBearerToken() }
                    } else {
                        httpHeaderRestTemplate.addHeaderGenerator(Headers.NAV_CONSUMER_TOKEN) { stsTokenValue.initBearerToken() }
                    }
                }

                if (AzureTokenService.supportedApplications.contains(applicationName)) {
                    val azureToken = hentAzureToken(applicationName)
                    httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { azureToken.initBearerToken() }
                }

                return RestTjeneste(ResttjenesteMedBaseUrl(httpHeaderRestTemplate, applicationUrl))
            } catch (throwable: Throwable) {
                CucumberTestRun.holdExceptionForTest(throwable)

                throw throwable
            }
        }

        private fun hentSaksbehandlerToken(applicationName: String): TokenValue {
            val tokenService: TokenService = when (CucumberTestRun.hentTokenType()) {
                TokenType.AZURE -> BidragCucumberSingletons.hentEllerInit(AzureTokenService::class) ?: throw notNullTokenService(TokenType.AZURE)
                TokenType.STS -> BidragCucumberSingletons.hentEllerInit(StsTokenService::class) ?: throw notNullTokenService(TokenType.STS)
            }

            return TokenValue(tokenService.cacheGeneratedToken(applicationName))
        }

        private fun hentStsToken(): TokenValue {
            val stsService: StsService = BidragCucumberSingletons.hentEllerInit(StsService::class)

            return if (CucumberTestRun.isNotSanityCheck) {
                TokenValue(stsService.hentServiceBrukerOidcToken() ?: throw IllegalStateException("Token er null!"))
            } else {
                TokenValue("sanity check, no token")
            }
        }

        private fun hentAzureToken(applicationName: String): TokenValue {
            val azureTokenService: AzureTokenService = BidragCucumberSingletons.hentEllerInit(AzureTokenService::class)

            return if (CucumberTestRun.isNotSanityCheck) {
                TokenValue(azureTokenService.generateToken(applicationName) ?: throw IllegalStateException("Token er null!"))
            } else {
                TokenValue("sanity check, no token")
            }
        }

        private fun notNullTokenService(tokenType: TokenType) = IllegalStateException("No service for $tokenType in spring context")
    }

    private lateinit var fullUrl: FullUrl
    private var responseEntity: ResponseEntity<String?>? = null

    fun hentFullUrlMedEventuellWarning() = "$fullUrl${appendWarningWhenExists()}"
    fun hentHttpHeaders(): HttpHeaders = responseEntity?.headers ?: HttpHeaders()
    fun hentHttpStatus(): HttpStatus = responseEntity?.statusCode?.value()?.let { HttpStatus.valueOf(it) } ?: HttpStatus.I_AM_A_TEAPOT
    fun hentResponse(): String? = responseEntity?.body
    fun hentResponseSomListe(): List<Any> = BidragCucumberSingletons.mapResponseSomListe(responseEntity)
    fun hentResponseSomMap() = BidragCucumberSingletons.mapResponseSomMap(responseEntity)

    private fun appendWarningWhenExists(): String {
        val warnings = responseEntity?.headers?.get(HttpHeaders.WARNING) ?: emptyList()

        return if (warnings.isNotEmpty()) " - ${warnings[0]}" else ""
    }

    fun exchangeGet(endpointUrl: String, failOnNotFound: Boolean = true, failOnBadRequest: Boolean = true): ResponseEntity<String?> {
        val header = initHttpHeadersWithCorrelationIdAndEnhet()

        exchange(
            jsonEntity = HttpEntity(null, header),
            endpointUrl = endpointUrl,
            httpMethod = HttpMethod.GET,
            failOnNotFound = failOnNotFound,
            failOnBadRequest = failOnBadRequest
        )

        LOGGER.info(
            if (responseEntity?.body != null) {
                "response with body and status ${responseEntity!!.statusCode}"
            } else if (responseEntity == null) "no response entity (${sanityCheck()})" else "no response body with status ${responseEntity!!.statusCode}"
        )

        return responseEntity ?: ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build()
    }

    fun exchangePatch(endpointUrl: String, journalpostJson: String, customHeaders: Array<out Pair<String, String>> = emptyArray()) {
        val jsonEntity = httpEntity(journalpostJson, customHeaders)
        exchange(jsonEntity, endpointUrl, HttpMethod.PATCH)
    }

    private fun sanityCheck(): String {
        return if (CucumberTestRun.isSanityCheck) "is sanity check" else "is NOT sanity check"
    }

    private fun initHttpHeadersWithCorrelationIdAndEnhet(customHeaders: Array<out Pair<String, String>> = emptyArray()): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(CorrelationId.CORRELATION_ID_HEADER, ScenarioManager.fetchCorrelationIdForScenario())
        headers.add(EnhetFilter.X_ENHET_HEADER, "4833")

        customHeaders.forEach {
            if (headers.containsKey(it.first)) {
                headers.remove(it.first)
            }

            headers.add(it.first, it.second)
        }

        return headers
    }

    fun exchangePost(endpointUrl: String, body: String, failOnBadRequest: Boolean = true, vararg customHeaders: Pair<String, String>) {
        val jsonEntity = httpEntity(body, customHeaders)
        exchange(jsonEntity = jsonEntity, endpointUrl = endpointUrl, httpMethod = HttpMethod.POST, failOnBadRequest = failOnBadRequest)
    }

    private fun httpEntity(body: Any, customHeaders: Array<out Pair<String, String>>): HttpEntity<*> {
        val headers = initHttpHeadersWithCorrelationIdAndEnhet(customHeaders)
        headers.contentType = MediaType.APPLICATION_JSON

        return HttpEntity(body, headers)
    }

    internal fun exchange(
        jsonEntity: HttpEntity<*>,
        endpointUrl: String,
        httpMethod: HttpMethod,
        failOnNotFound: Boolean = true,
        failOnBadRequest: Boolean = true
    ) {
        fullUrl = FullUrl(rest.baseUrl, endpointUrl)
        LOGGER.info("$httpMethod: $fullUrl")

        try {
            responseEntity = rest.template.exchange(endpointUrl, httpMethod, jsonEntity, String::class.java)
        } catch (e: Exception) {
            responseEntity = if (e is HttpStatusCodeException) {
                ResponseEntity.status(e.statusCode).body<String>(failure(jsonEntity.body, e))
            } else {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<String>(failure(jsonEntity.body, e))
            }

            if (isError(e = e, failOn404 = failOnNotFound, failOnBadRequest = failOnBadRequest)) {
                ScenarioManager.errorLog(">>> $httpMethod FEILET! ($fullUrl) ${failure(jsonEntity.body, e)}", e)

                if (CucumberTestRun.isNotSanityCheck) {
                    throw e
                }
            }
        }
    }

    private fun isError(e: Exception, failOn404: Boolean, failOnBadRequest: Boolean): Boolean {
        if (isNotFound(e)) {
            return failOn404
        }

        if (isBadRequest(e)) {
            return failOnBadRequest
        }

        return true
    }

    private fun isBadRequest(e: Exception) = e is HttpStatusCodeException && e.statusCode == HttpStatus.BAD_REQUEST
    private fun isNotFound(e: Exception) = e is HttpStatusCodeException && e.statusCode == HttpStatus.NOT_FOUND
    private fun failure(body: Any?, e: Exception) = """-
    - input body: $body
    - exception : "${e::class.simpleName}: ${e.message}"
    """.trimIndent()

    @Suppress("UNCHECKED_CAST")
    fun hentResponseSomListeAvStrenger(): List<String> {
        return BidragCucumberSingletons.mapResponseSomListe(responseEntity) as List<String>
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
