package no.nav.bidrag.cucumber.sikkerhet

import no.nav.bidrag.cucumber.Environment

object OidcConfiguration {

    private const val BIDRAG_UI_FEATURE_OIDC = "bidrag-ui-feature-oidc"
    private const val BIDRAG_UI_OIDC = "bidrag-ui-q2"

    fun fetchConfiguration() = if (Environment.isFeatureBranch)
        OidcConfigEnvironment(
            agentName = BIDRAG_UI_FEATURE_OIDC,
            tokenNamespace = "feature-q1",
            issoRedirectUrl = "https://bidrag-ui-feature.dev.adeo.no/isso"
        )
    else
        OidcConfigEnvironment(
            agentName = BIDRAG_UI_OIDC,
            tokenNamespace = "q2",
            issoRedirectUrl = "https://bidrag-ui.dev.adeo.no/isso"
        )
}

data class OidcConfigEnvironment(
    val agentName: String,
    val tokenNamespace: String,
    val issoRedirectUrl: String
)