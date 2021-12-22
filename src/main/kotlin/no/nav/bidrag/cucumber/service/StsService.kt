package no.nav.bidrag.cucumber.service

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.Headers
import no.nav.bidrag.cucumber.STS_PASSWORD
import no.nav.bidrag.cucumber.STS_URL
import no.nav.bidrag.cucumber.STS_USER
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service

@Service
class StsService(private val basicAuthRestTemplate: HttpHeaderRestTemplate) {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(StsService::class.java)
        val supportedApplications = setOf("dokarkiv-api", "bidrag-dokument-arkivering")
    }

    init {
        basicAuthRestTemplate.addHeaderGenerator(Headers.BASIC_PASS) { Environment.fetchPropertyOrEnvironment(STS_PASSWORD) }
        basicAuthRestTemplate.addHeaderGenerator(Headers.BASIC_USER) { Environment.fetchPropertyOrEnvironment(STS_USER) }
    }

    fun hentServiceBrukerOidcToken(): String? {
        val stsUser = Environment.fetchPropertyOrEnvironment(STS_USER)
        LOGGER.info("Hent service bruker token for $stsUser")

        val responseEntity = basicAuthRestTemplate.exchange(
            "${Environment.fetchPropertyOrEnvironment(STS_URL)}/rest/v1/sts/token2", HttpMethod.GET, null, Map::class.java
        )

        if (!responseEntity.statusCode.is2xxSuccessful) {
            LOGGER.warn("Kunne ikke hente token til $stsUser, http status: ${responseEntity.statusCode}!")
        }

        val token = responseEntity.body?.get("idToken") as String?

        if (token == null || token.isBlank()) {
            LOGGER.warn(
                """Ikke et generet token....
                - null  : ${(token == null)}
                - blank : ${token?.isBlank()}
                - length: ${token?.length}
                - body  : ${responseEntity.body?.keys}
                """.trimIndent()
            )
        }

        return token
    }
}
