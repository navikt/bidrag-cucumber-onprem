package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.sikkerhet.OidcConfiguration
import no.nav.bidrag.cucumber.sikkerhet.OidcTokenManager
import no.nav.bidrag.cucumber.sikkerhet.TokenService
import org.springframework.stereotype.Service

@Service
class OidcTokenService(private val oidcTokenManager: OidcTokenManager) : TokenService {
    override fun generateBearerToken(application: String): String {
        val token = CucumberTestRun.fetchToken()

        if (token != null) {
            return initBearer(token)
        }

        val oidcConfigEnvironment = OidcConfiguration.fetchConfiguration()
        val generatedToken = oidcTokenManager.generateToken(Environment.navUsername, oidcConfigEnvironment)
        CucumberTestRun.setGeneratedToken(generatedToken)

        return initBearer(generatedToken)
    }

    private fun initBearer(token: String) = "Bearer $token"
}