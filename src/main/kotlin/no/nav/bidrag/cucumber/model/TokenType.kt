package no.nav.bidrag.cucumber.model

enum class TokenType {
    AZURE, STS;

    companion object {
        internal fun fetch(tokenType: String): TokenType = values().first { it.name == tokenType }
    }
}
