package no.nav.bidrag.cucumber.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Fasit.ALIAS_BIDRAG_UI
import no.nav.bidrag.cucumber.Fasit.ALIAS_OIDC
import no.nav.bidrag.cucumber.Headers
import no.nav.bidrag.cucumber.Url
import no.nav.bidrag.cucumber.model.BaseUrlTemplateHandler
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.sikkerhet.FasitManager
import no.nav.bidrag.cucumber.sikkerhet.FasitRessurs
import no.nav.bidrag.cucumber.sikkerhet.OidcConfiguration
import org.apache.tomcat.util.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class OidcTokenService(private val applicationContext: ApplicationContext, private val fasitManager: FasitManager) : TokenService() {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(OidcTokenService::class.java)
    }

    override fun generateToken(application: String) = generateOidcToken(CucumberTestRun.navUsername ?: throw noUserForToken())
    private fun noUserForToken() = IllegalStateException("No navUsername to generate token for!")

    fun generateOidcToken(navUsername: String): String {
        try {
//            val openIdFasitRessurs = hentOpenIdConnectFasitRessurs()
//            val openAmPassword = hentOpenAmPassword(navUsername, openIdFasitRessurs)
//            val tokenIdForTestUser = hentTokenIdForTestbruker()
//            val codeFraQueryString = fetchCodeFromCodeQueryString(tokenIdForTestUser)
//
//            LOGGER.info("Fetched id token for ${CucumberTestRun.testUsername}")
              return "eyAidHlwIjogIkpXVCIsICJraWQiOiAiMWwySmtDb1RMMTBibWVBeHlsZzR4Umk4ajJZPSIsICJhbGciOiAiUlMyNTYiIH0.eyAiYXRfaGFzaCI6ICJ6cVNPTUVpbHh5Y1BqSEppWi1ETTRRIiwgInN1YiI6ICJaOTk0OTc3IiwgImF1ZGl0VHJhY2tpbmdJZCI6ICI0ZDRiOWJjYi05ZTc2LTQ5ZTUtYTFlZS05YjBiNmE0OWMwZWYtMTkyMzE2MiIsICJpc3MiOiAiaHR0cHM6Ly9pc3NvLXEuYWRlby5ubzo0NDMvaXNzby9vYXV0aDIiLCAidG9rZW5OYW1lIjogImlkX3Rva2VuIiwgImF1ZCI6ICJiaWRyYWctdWktZmVhdHVyZS1xMSIsICJvcmcuZm9yZ2Vyb2NrLm9wZW5pZGNvbm5lY3Qub3BzIjogIjBiMWUwYzZkLWUwMGItNGJiNS05YmYzLTJmMGNmZDA1YjRlMSIsICJhenAiOiAiYmlkcmFnLXVpLWZlYXR1cmUtcTEiLCAiYXV0aF90aW1lIjogMTY1Mjg2OTEwNiwgInJlYWxtIjogIi8iLCAiZXhwIjogMTY1Mjg3MjcwNiwgInRva2VuVHlwZSI6ICJKV1RUb2tlbiIsICJpYXQiOiAxNjUyODY5MTA2IH0.DXbT2hIrVizzo8Y0Vve2Z4V5yl8FqAvyvYVZ58E9yyWLwLvfkiVrH1P2AX1rtEIsNf5KN5nKC047mTAjdvmuZ7-sgpMc8-aFnIXW4IdNot6z6sjuGRrGdxuubSgq4rCZ_Wu5A8ZHHGnFEBK1zdFrG2hEClMiTHMRbeqSsdjRyUPBZOucH3OVHQxysoxMFSjo3x66SFtPu-xy84lfJalknJ8JEppHasxrEu5B7hrp6oRQtZpWO2YuyyVskTEDinMJP_I-MvBfb5sTlNCz33piH3FsPuWbKnymNvkoqBWQHho5l48MDMBGROvIoZ92VboePrK6L_oSrfTXUhb1fuN-AA"
        } catch (throwable: Throwable) {
            LOGGER.error(
                "Unable to find token id for ${CucumberTestRun.testUsername} using $navUsername - ${throwable.javaClass.name}: ${throwable.message}"
            )

            throw throwable
        }
    }

    private fun hentOpenIdConnectFasitRessurs(): FasitRessurs {
        val fasitRessursUrl = UriComponentsBuilder.fromHttpUrl(Url.FASIT)
            .query("type=OpenIdConnect")
            .query("environment=${CucumberTestRun.qEnvironment}")
            .query("alias=$ALIAS_OIDC")
            .query("zone=fss")
            .query("usage=false")
            .toUriString()

        val openIdConnectFasitRessurs = fasitManager.hentFasitRessurs(fasitRessursUrl, ALIAS_OIDC)

        LOGGER.info("Hentet openIdConnectFasitRessurs: $openIdConnectFasitRessurs")

        return openIdConnectFasitRessurs
    }

    private fun hentOpenAmPassword(navUsername: String, openIdFasitManagerRessurs: FasitRessurs): String {
        val basicAuth = "$navUsername:${Environment.navAuth}"
        val httpEntityWithAuthorizationHeader = initHttpEntity(
            header(HttpHeaders.AUTHORIZATION, "Basic " + String(Base64.encodeBase64(basicAuth.toByteArray(Charsets.UTF_8))))
        )

        LOGGER.info("Finding OpenAM password for $navUsername from ${openIdFasitManagerRessurs.passordUrl()}")

        val responseEntity = initRestTemplate(openIdFasitManagerRessurs.passordUrl())
            .exchange("/", HttpMethod.GET, httpEntityWithAuthorizationHeader, String::class.java)

        if (responseEntity.statusCode != HttpStatus.OK) {
            LOGGER.error(
                "ERROR finding OpenAM password (${openIdFasitManagerRessurs.passordUrl()}), status: ${responseEntity.statusCode}, body: ${
                    responseEntity.body
                }"
            )
        }

        return responseEntity.body ?: throw IllegalStateException("fant ikke passord for bruker på open am")
    }

    private fun hentTokenIdForTestbruker(): String {
        val testUser = CucumberTestRun.testUsername ?: throw IllegalStateException("Cannot provide security without username for test user")
        val testUserAndAgent = "$testUser: agent: ${OidcConfiguration.fetchConfiguration().agentName}"
        val httpEntityWithHeaders = initHttpEntity(
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/json"),
            header(Headers.X_OPENAM_USER, testUser),
            header(Headers.X_OPENAM_PASSW, Environment.testUserAuth)
        )

        LOGGER.info("Hent token id for $testUserAndAgent from ${Url.ISSO}")

        val responseEntity = initRestTemplate().exchange(Url.ISSO, HttpMethod.POST, httpEntityWithHeaders, String::class.java)

        if (responseEntity.statusCode != HttpStatus.OK) throw IllegalStateException(
            "Unable to find token id, status: ${responseEntity.statusCode}, body: ${
                responseEntity.body
            }"
        )

        val authJson = responseEntity.body ?: throw IllegalStateException("fant ikke json for $testUserAndAgent")
        val authMap = ObjectMapper().readValue(authJson, Map::class.java)

        LOGGER.info("Setting up security for $testUserAndAgent")

        return authMap["tokenId"] as String? ?: throw IllegalStateException("Fant ikke id token i json for $testUserAndAgent")
    }

    private fun fetchCodeFromCodeQueryString(tokenIdForAuthenticatedTestUser: String): String {
        val oidcConfigEnvironment = OidcConfiguration.fetchConfiguration()
        val bidragUiTokenNamespace = "bidrag-ui-${oidcConfigEnvironment.tokenNamespace}"
        val redirectUrl = oidcConfigEnvironment.issoRedirectUrl
        val body =
            "client_id=$bidragUiTokenNamespace&response_type=code&redirect_uri=$redirectUrl&decision=allow&csrf=$tokenIdForAuthenticatedTestUser&scope=openid"

        LOGGER.info("Henter location uri med $body...")

        val httpEntityWithHeaders = initHttpEntity(
            body = body,
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"),
            header(HttpHeaders.COOKIE, "nav-isso=$tokenIdForAuthenticatedTestUser")
        )

        val uri = initRestTemplate().postForLocation(Url.ISSO_AUTHORIZE, httpEntityWithHeaders) ?: throw IllegalStateException(
            "fant ikke location uri"
        )

        val queryString = uri.query
        val queries = queryString.split("&")
        val codeQuery = queries.find { it.startsWith("code=") } ?: throw IllegalStateException(
            "Fant ikke code i query string til ${uri.toString().substring(0, uri.toString().indexOf('?'))}"
        )

        return codeQuery.substringAfter("code=")
    }

    private fun hentIdToken(codeFraLocationHeader: String, passordOpenAm: String): String {
        val oidcConfigEnvironment = OidcConfiguration.fetchConfiguration()
        val openApAuth = "$ALIAS_BIDRAG_UI-${oidcConfigEnvironment.tokenNamespace}:$passordOpenAm"
        val httpEntityWithHeaders = initHttpEntity(
            body = "grant_type=authorization_code&code=$codeFraLocationHeader&redirect_uri=${oidcConfigEnvironment.issoRedirectUrl}",
            header(HttpHeaders.AUTHORIZATION, "Basic " + String(Base64.encodeBase64(openApAuth.toByteArray(Charsets.UTF_8)))),
            header(HttpHeaders.CACHE_CONTROL, "no-cache"),
            header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
        )

        val responseEntity = initRestTemplate().exchange(Url.ISSO_ACCESS_TOKEN, HttpMethod.POST, httpEntityWithHeaders, String::class.java)

        if (!responseEntity.statusCode.is2xxSuccessful) {
            LOGGER.error("ERROR finding id_token from ${Url.ISSO_ACCESS_TOKEN}), status: ${responseEntity.statusCode}")
        }

        val accessTokenJson = responseEntity.body ?: throw IllegalStateException("fant ikke json med id token")
        val accessTokenMap = ObjectMapper().readValue(accessTokenJson, Map::class.java)

        return accessTokenMap["id_token"] as String? ?: throw IllegalStateException("fant ikke id_token i json")
    }

    private fun header(headerName: String, headerValue: String): Map.Entry<String, String> {
        return java.util.Map.of(headerName, headerValue).entries.first()
    }

    private fun initHttpEntity(vararg headers: Map.Entry<String, String>): HttpEntity<*> {
        return initHttpEntity(body = null, *headers)
    }

    private fun initHttpEntity(body: String?, vararg headers: Map.Entry<String, String>): HttpEntity<*> {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        headers.forEach { linkedMultiValueMap.add(it.key, it.value) }
        val httpHeaders = HttpHeaders(linkedMultiValueMap)

        return HttpEntity(body, httpHeaders)
    }

    private fun initRestTemplate(url: String? = null): RestTemplate {
        val restTemplate = applicationContext.getBean(RestTemplate::class.java)

        if (url != null) {
            restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(url)
        }

        return restTemplate
    }
}
