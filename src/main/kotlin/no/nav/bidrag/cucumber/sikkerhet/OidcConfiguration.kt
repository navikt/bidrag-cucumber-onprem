package no.nav.bidrag.cucumber.sikkerhet

object OidcConfiguration {

    const val BIDRAG_UI_OIDC = "bidrag-ui-q2"
    const val BIDRAG_UI_FEATURE_OIDC = "bidrag-ui-feature-oidc"

    fun fetchConfiguration(application: String) = if (application.contains("-feature"))
        OidcConfigEnvironment(agentName = BIDRAG_UI_FEATURE_OIDC)
    else
        OidcConfigEnvironment(agentName = BIDRAG_UI_OIDC)

    data class OidcConfigEnvironment(
        var agentName: String,
        val hostUrl: String = "https://isso-q.adeo.no:443/isso",
        val issuerUrl: String = "https://isso-q.adeo.no:443/isso/oauth2",
        val jwksUrl: String = "https://isso-q.adeo.no:443/isso/oauth2/connect/jwk_uri"
    )
}
