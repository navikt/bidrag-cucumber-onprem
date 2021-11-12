package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.sikkerhet.TokenService
import org.springframework.stereotype.Service

@Service
class OidcTokenService: TokenService {
    override fun generateBearerToken(application: String): String {
        TODO("Not yet implemented")
    }
}