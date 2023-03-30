package no.nav.bidrag.cucumber.service

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.stereotype.Service

@Service
class AzureTokenService(val authorizedClientManager: OAuth2AuthorizedClientManager) : TokenService() {

    companion object {
        @JvmStatic
        private val ANONYMOUS_AUTHENTICATION: Authentication = AnonymousAuthenticationToken(
            "anonymous",
            "anonymousUser",
            AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
        )
        val supportedApplications = setOf("oppgave-api", "bidrag-sak", "bidrag-dokument-arkiv", "bidrag-dokument-journalpost", "bidrag-person", "bidrag-organisasjon", "bidrag-dokument", "bidrag-dokument-arkivering")
    }

    override fun generateToken(application: String): String {
        return authorizedClientManager
            .authorize(
                OAuth2AuthorizeRequest
                    .withClientRegistrationId(application)
                    .principal(ANONYMOUS_AUTHENTICATION)
                    .build()
            )!!.accessToken.tokenValue
    }
}
