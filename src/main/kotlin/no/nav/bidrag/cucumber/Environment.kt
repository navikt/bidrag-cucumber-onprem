package no.nav.bidrag.cucumber

import org.slf4j.LoggerFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplateHandler
import java.io.File
import java.net.URI

internal const val ENVIRONMENT_FEATURE = "feature"
internal const val ENVIRONMENT_MAIN = "main"
private val LOGGER = LoggerFactory.getLogger(Environment::class.java)

internal object Environment {

    internal val miljo by lazy { fetchEnvironment(ENVIRONMENT, "Fant ikke miljø for kjøring") }
    internal val naisProjectFolder: String by lazy { fetchEnvironment(PROJECT_NAIS_FOLDER, "Det er ikke oppgitt ei mappe for nais prosjekt") }

    fun fetchEnvironment(environment: String, errorMessage: String) =
        System.getProperty(environment) ?: System.getenv()[environment] ?: throw IllegalStateException(errorMessage)

    private fun findPossibleNaisApplications(): Set<String> {
        val discoveredPossibleNaisApplications = HashSet<String>()

        File(naisProjectFolder).walk().forEach {
            if (it.isDirectory) {
                discoveredPossibleNaisApplications.add(it.name)
            }
        }

        return discoveredPossibleNaisApplications
    }

    fun testUser() = System.getProperty(CREDENTIALS_TEST_USER) ?: throw IllegalStateException("Fant ikke testbruker (ala z123456)")
    fun testAuthentication() = System.getProperty(CREDENTIALS_TEST_USER_AUTH)
        ?: throw IllegalStateException("Fant ikke passord til ${testUser()}")

    fun user() = System.getProperty(CREDENTIALS_USERNAME) ?: throw IllegalStateException("Fant ikke nav-bruker (ala [x]123456)")
    fun userAuthentication() = System.getProperty(CREDENTIALS_USER_AUTH) ?: throw IllegalStateException("Fant ikke passord til ${user()}")

    internal fun <T : RestTemplate> setBaseUrlPa(restTemplate: T, url: String): T {
        restTemplate.uriTemplateHandler = BaseUrlTemplateHandler(url)

        return restTemplate
    }
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