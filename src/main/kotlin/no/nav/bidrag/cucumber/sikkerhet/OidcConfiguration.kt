package no.nav.bidrag.cucumber.sikkerhet

import no.nav.bidrag.cucumber.model.CucumberTestRun

object OidcConfiguration {

    fun fetchConfiguration() = if (CucumberTestRun.isFeatureBranch)
        OidcConfigEnvironment(
            agentName = "bidrag-ui-feature-oidc",
            tokenNamespace = "feature-q1",
            issoRedirectUrl = "https://bidrag-ui-feature.dev.adeo.no/isso"
        )
    else
        OidcConfigEnvironment(
            agentName = "bidrag-ui-q2",
            tokenNamespace = "q2",
            issoRedirectUrl = "https://bidrag-ui.dev.adeo.no/isso"
        )
}

data class OidcConfigEnvironment(
    val agentName: String,
    val tokenNamespace: String,
    val issoRedirectUrl: String
)