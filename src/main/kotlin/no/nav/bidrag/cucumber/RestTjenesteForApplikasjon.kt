package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.model.BidragCucumberSingletons
import no.nav.bidrag.cucumber.model.TokenType
import no.nav.bidrag.cucumber.model.TokenType.AZURE
import no.nav.bidrag.cucumber.model.TokenType.OIDC
import no.nav.bidrag.cucumber.service.AzureTokenService
import no.nav.bidrag.cucumber.service.OidcTokenService
import no.nav.bidrag.cucumber.sikkerhet.TokenService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.web.util.UriTemplateHandler
import java.net.URI

internal object RestTjenesteForApplikasjon {
    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(RestTjenesteForApplikasjon::class.java)
    private val REST_TJENESTE_FOR_APPLIKASJON = RestTjenesteForApplikasjonThreadLocal()

    fun hentEllerKonfigurer(applicationName: String): RestTjeneste.ResttjenesteMedBaseUrl {
        return REST_TJENESTE_FOR_APPLIKASJON.hentEllerKonfigurer(applicationName) { konfigurer(applicationName) }
    }

    private fun konfigurer(applicationName: String): RestTjeneste.ResttjenesteMedBaseUrl {
        return konfigurerSikkerhet(applicationName, konfigurerApplikasjonUrl(applicationName))
    }

    internal fun konfigurerApplikasjonUrl(applicationName: String): String {
        val ingress = Environment.fetchIngress(applicationName)

        if (Environment.isNoContextPathForApp(applicationName)) {
            return ingress
        }

        val ingressUrl = if (ingress.endsWith('/')) ingress.removeSuffix("/") else ingress

        return "$ingressUrl/$applicationName"
    }

    private fun konfigurerSikkerhet(applicationName: String, applicationUrl: String): RestTjeneste.ResttjenesteMedBaseUrl {

        val httpHeaderRestTemplate = BidragCucumberSingletons.hentPrototypeFraApplicationContext()
        httpHeaderRestTemplate.uriTemplateHandler = BaseUrlTemplateHandler(applicationUrl)

        if (Environment.isTestUserPresent) {
            val tokenService = when(Environment.hentTokeType()) {
                AZURE -> BidragCucumberSingletons.hentFraContext(AzureTokenService::class) as TokenService? ?: throw notNullTokenService(AZURE)
                OIDC -> BidragCucumberSingletons.hentFraContext(OidcTokenService::class) as TokenService? ?: throw notNullTokenService(OIDC)
            }

            httpHeaderRestTemplate.addHeaderGenerator(HttpHeaders.AUTHORIZATION) { tokenService.generateBearerToken(applicationName) }
        } else {
            LOGGER.info("No user to provide security for when accessing $applicationName")
        }

        return RestTjeneste.ResttjenesteMedBaseUrl(httpHeaderRestTemplate, applicationUrl)
    }

    private fun notNullTokenService(tokenType: TokenType) = IllegalStateException("No token service provided for $tokenType")

    fun removeAll() {
        REST_TJENESTE_FOR_APPLIKASJON.removeAll()
    }

    private class BaseUrlTemplateHandler(val baseUrl: String) : UriTemplateHandler {
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

    class RestTjenesteForApplikasjonThreadLocal {
        companion object {
            @JvmStatic
            private val REST_TJENESTER = ThreadLocal<MutableMap<String, RestTjeneste.ResttjenesteMedBaseUrl>>()
        }

        fun hentEllerKonfigurer(applicationName: String, konfigurer: () -> RestTjeneste.ResttjenesteMedBaseUrl): RestTjeneste.ResttjenesteMedBaseUrl {
            val tradensResttjenester = hentEllerLag()
            return tradensResttjenester.computeIfAbsent(applicationName) { konfigurer() }
        }

        private fun hentEllerLag(): MutableMap<String, RestTjeneste.ResttjenesteMedBaseUrl> {
            if (REST_TJENESTER.get() == null) {
                REST_TJENESTER.set(HashMap())
            }

            return REST_TJENESTER.get()
        }

        fun removeAll() {
            REST_TJENESTER.remove()
        }
    }
}
