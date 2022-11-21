package no.nav.bidrag.cucumber.service

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.stereotype.Service

@Service
class StsTokenService(val stsService: StsService) : TokenService() {

    override fun generateToken(application: String): String {
        return stsService.hentServiceBrukerOidcToken()?: throw IllegalStateException("Fant ingen STS token")
    }
}