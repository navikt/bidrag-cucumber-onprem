package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.model.FilePath

// constants for input via System.getProperty(...)/System.getenv(...)
internal const val INGRESSES_FOR_APPS = "INGRESSES_FOR_APPS"
internal const val NO_CONTEXT_PATH_FOR_APPS = "NO_CONTEXT_PATH_FOR_APPS"
internal const val SECURITY_TOKEN = "SECURITY_TOKEN"
internal const val NAV_AUTH = "NAV_AUTH"
internal const val NAV_USER = "NAV_USER"
internal const val STS_PASSWORD = "STS_PASSWORD"
internal const val STS_URL = "STS_URL"
internal const val STS_USER = "STS_USER"
internal const val TAGS = "TAGS"
internal const val TEST_AUTH = "TEST_AUTH"
internal const val TEST_USER = "TEST_USER"
internal const val SANITY_CHECK = "SANITY_CHECK"

// spring configuration
internal const val PROFILE_LIVE = "LIVE"

object Url {
    internal const val FASIT = "https://fasit.adeo.no/api/v2/resources"
    internal const val ISSO = "https://isso-q.adeo.no:443/isso/json/authenticate?authIndexType=service&authIndexValue=ldapservice"
    internal const val ISSO_ACCESS_TOKEN = "https://isso-q.adeo.no:443/isso/oauth2/access_token"
    internal const val ISSO_AUTHORIZE = "https://isso-q.adeo.no/isso/oauth2/authorize"
}

object Headers {
    internal const val BASIC_PASS = "password"
    internal const val BASIC_USER = "username"
    internal const val NAV_CALL_ID = "NAV-Call-id"
    internal const val NAV_CONSUMER_TOKEN = "Nav-Consumer-Token"
    internal const val X_OPENAM_PASSW = "X-OpenAM-Password"
    internal const val X_OPENAM_USER = "X-OpenAM-Username"
}

object Fasit {
    internal const val ALIAS_BIDRAG_UI = "bidrag-ui"
    internal const val ALIAS_OIDC = "$ALIAS_BIDRAG_UI-oidc"
}

// misc configuration
internal const val CORRELATION_ID = "correlationId"

internal val ABSOLUTE_FEATURE_PATH = FilePath("features.path").findFolderPath()
