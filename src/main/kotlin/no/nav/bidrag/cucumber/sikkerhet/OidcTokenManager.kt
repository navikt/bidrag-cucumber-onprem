package no.nav.bidrag.cucumber.sikkerhet

import org.springframework.stereotype.Component

@Component
class OidcTokenManager {
    fun generateToken(navUsername: String?, oidcConfigEnvironment: OidcConfiguration.OidcConfigEnvironment): String {
        TODO("Not yet implemented")
    }
}
