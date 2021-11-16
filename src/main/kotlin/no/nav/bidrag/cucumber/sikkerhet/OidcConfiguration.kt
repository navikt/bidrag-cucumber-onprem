package no.nav.bidrag.cucumber.sikkerhet

object OidcConfiguration {

    private const val BIDRAG_UI_FEATURE_OIDC = "bidrag-ui-feature-oidc"
    private const val BIDRAG_UI_OIDC = "bidrag-ui-q2"

    fun fetchConfiguration(application: String) = if (application.contains("-feature"))
        OidcConfigEnvironment(
            agentName = BIDRAG_UI_FEATURE_OIDC,
            featureBranch = true,
            tokenNamespace = "feature-q1",
        issoRedirectUrl = "https://bidrag-ui-feature.dev.adeo.no/isso"
        )
    else
        OidcConfigEnvironment(
            agentName = BIDRAG_UI_OIDC,
            featureBranch = false,
            tokenNamespace = "q2",
            issoRedirectUrl = "https://bidrag-ui.dev.adeo.no/isso"
        )
}

data class OidcConfigEnvironment(
    val agentName: String,
    val featureBranch: Boolean,
    val tokenNamespace: String,
    val issoRedirectUrl: String
)