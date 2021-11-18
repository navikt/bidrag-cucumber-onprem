package no.nav.bidrag.cucumber.sikkerhet

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.cucumber.ALIAS_BIDRAG_UI
import no.nav.bidrag.cucumber.ALIAS_OIDC
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Headers
import no.nav.bidrag.cucumber.Url
import no.nav.bidrag.cucumber.model.BaseUrlTemplateHandler
import org.apache.tomcat.util.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class OidcTokenManager {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(OidcTokenManager::class.java)
    }

    fun generateToken(navUsername: String?, oidcConfigEnvironment: OidcConfigEnvironment): String {
        val openIdFasitRessurs = hentOpenIdConnectFasitRessurs(oidcConfigEnvironment)
        val openAmPassword = hentOpenAmPassword(navUsername, Environment.navAuth, openIdFasitRessurs)
        val tokenIdForTestUser = hentTokenIdForTestbruker(oidcConfigEnvironment)
        val codeFraLocationHeader = hentCodeFraLocationHeader(tokenIdForTestUser, oidcConfigEnvironment)

        LOGGER.info("Fetched id token for ${Environment.testUsername}")

        return hentIdToken(codeFraLocationHeader, openAmPassword, oidcConfigEnvironment)
    }

    private fun hentOpenIdConnectFasitRessurs(oidcConfigEnvironment: OidcConfigEnvironment): Fasit.FasitRessurs {
        val fasitRessursUrl = Fasit.buildUriString(
            Url.FASIT, "type=OpenIdConnect", "environment=${oidcConfigEnvironment}", "alias=$ALIAS_OIDC", "zone=fss ", "usage=false"
        )

        val openIdConnectFasitRessurs = Fasit.hentFasitRessurs(fasitRessursUrl, ALIAS_OIDC)

        LOGGER.info("Hentet openIdConnectFasitRessurs: $openIdConnectFasitRessurs")

        return openIdConnectFasitRessurs
    }

    private fun hentOpenAmPassword(navUsername: String?, navAuth: String, openIdFasitRessurs: Fasit.FasitRessurs): String {
        val httpEntityWithAuthorizationHeader = initHttpEntity(
            header(HttpHeaders.AUTHORIZATION, "Basic " + String(Base64.encodeBase64(navAuth.toByteArray(Charsets.UTF_8))))
        )

        LOGGER.info("Finding OpenAM password for $navUsername from ${openIdFasitRessurs.passordUrl()}")

        return initRestTemplate(openIdFasitRessurs.passordUrl())
            .exchange("/", HttpMethod.GET, httpEntityWithAuthorizationHeader, String::class.java)
            .body ?: throw IllegalStateException("fant ikke passord for bruker på open am")
    }

    private fun hentTokenIdForTestbruker(oidcConfigEnvironment: OidcConfigEnvironment): String {
        val testUser = Environment.testUsername ?: throw IllegalStateException("Cannot provide security without user")
        val testUserAndAgent = "$testUser: agent: ${oidcConfigEnvironment.agentName}"
        val httpEntityWithHeaders = initHttpEntity(
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/json"),
            header(Headers.X_OPENAM_USER, testUser),
            header(Headers.X_OPENAM_PASSW, Environment.testUserAuth)
        )

        LOGGER.info("Hent token id for $testUserAndAgent from ${Url.ISSO}")

        val authJson = RestTemplate().exchange(Url.ISSO, HttpMethod.POST, httpEntityWithHeaders, String::class.java)
            .body ?: throw IllegalStateException("fant ikke json for $testUserAndAgent")

        val authMap = ObjectMapper().readValue(authJson, Map::class.java)

        LOGGER.info("Setting up security for $testUserAndAgent")

        return authMap["tokenId"] as String? ?: throw IllegalStateException("Fant ikke id token i json for $testUserAndAgent")
    }

    private fun hentCodeFraLocationHeader(tokenIdForAuthenticatedTestUser: String, oidcConfigEnvironment: OidcConfigEnvironment): String {
        val tokenNamespace = oidcConfigEnvironment.tokenNamespace
        val issoRedirectUrl = oidcConfigEnvironment.issoRedirectUrl

        val httpEntityWithHeaders = initHttpEntity(
            "client_id=bidrag-ui-$tokenNamespace&response_type=code&redirect_uri=$issoRedirectUrl&decision=allow&csrf=$tokenIdForAuthenticatedTestUser&scope=openid",
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"),
            header(HttpHeaders.COOKIE, "nav-isso=$tokenIdForAuthenticatedTestUser")
        )

        val uri = RestTemplate().postForLocation(Url.ISSO_AUTHORIZE, httpEntityWithHeaders) ?: throw IllegalStateException("fant ikke location uri")

        val queryString = uri.query
        val queries = queryString.split("&")
        val codeQuery = queries.find { it.startsWith("code=") } ?: throw IllegalStateException("Fant ikke code i Location")

        return codeQuery.substringAfter("code=")
    }

    private fun hentIdToken(codeFraLocationHeader: String, passordOpenAm: String, oidcConfigEnvironment: OidcConfigEnvironment): String {
        val openApAuth = "$ALIAS_BIDRAG_UI-${oidcConfigEnvironment.tokenNamespace}:$passordOpenAm"
        val httpEntityWithHeaders = initHttpEntity(
            "grant_type=authorization_code&code=$codeFraLocationHeader&redirect_uri=${oidcConfigEnvironment.issoRedirectUrl}",
            header(HttpHeaders.AUTHORIZATION, "Basic " + String(Base64.encodeBase64(openApAuth.toByteArray(Charsets.UTF_8)))),
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
        )

        val accessTokenJson = RestTemplate().exchange(Url.ISSO_ACCESS_TOKEN, HttpMethod.POST, httpEntityWithHeaders, String::class.java)
            .body ?: throw IllegalStateException("fant ikke json med id token")

        val accessTokenMap = ObjectMapper().readValue(accessTokenJson, Map::class.java)

        return accessTokenMap["id_token"] as String? ?: throw IllegalStateException("fant ikke id_token i json")
    }

    private fun header(headerName: String, headerValue: String): Map.Entry<String, String> {
        return java.util.Map.of(headerName, headerValue).entries.first()
    }

    private fun initHttpEntity(vararg headers: Map.Entry<String, String>): HttpEntity<*> {
        return initHttpEntity(null, *headers)
    }

    private fun initHttpEntity(data: String?, vararg headers: Map.Entry<String, String>): HttpEntity<*> {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        headers.forEach { linkedMultiValueMap.add(it.key, it.value) }
        val httpHeaders = HttpHeaders(linkedMultiValueMap)

        return HttpEntity(data, httpHeaders)
    }

    private fun initRestTemplate(url: String): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(url)

        return restTemplate
    }
}
