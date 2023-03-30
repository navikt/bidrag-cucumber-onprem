package no.nav.bidrag.cucumber.service

import org.springframework.stereotype.Service

@Service
class StsTokenService(val stsService: StsService) : TokenService() {

    override fun generateToken(application: String): String {
        return stsService.hentServiceBrukerOidcToken() ?: throw IllegalStateException("Fant ingen STS token")
    }
}
