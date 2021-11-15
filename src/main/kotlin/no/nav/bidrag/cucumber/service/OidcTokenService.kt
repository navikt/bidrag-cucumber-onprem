package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.sikkerhet.OidcConfiguration
import no.nav.bidrag.cucumber.sikkerhet.OidcTokenManager
import no.nav.bidrag.cucumber.sikkerhet.TokenService
import org.springframework.stereotype.Service

@Service
class OidcTokenService(private val oidcTokenManager: OidcTokenManager) : TokenService {
    companion object {
        @JvmStatic
        private val GENERATED_TOKEN = ThreadLocal<String>()

        fun fjernToken() {
            GENERATED_TOKEN.remove()
        }
    }

    override fun generateBearerToken(application: String): String {
        val token = GENERATED_TOKEN.get()

        if (token != null) {
            return bearer(token)
        }

        val oidcConfigEnvironment = OidcConfiguration.fetchConfiguration(application)
        val generatedToken = oidcTokenManager.generateToken(Environment.navUsername, oidcConfigEnvironment)
        GENERATED_TOKEN.set(generatedToken)

        return bearer(generatedToken)
    }

    private fun bearer(token: String) = "Bearer $token"
}