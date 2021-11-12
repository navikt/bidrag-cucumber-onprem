package no.nav.bidrag.cucumber.sikkerhet

interface TokenService {
    fun generateBearerToken(application: String): String
}