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

object Headers {
    internal const val BASIC_PASS = "password"
    internal const val BASIC_USER = "username"
    internal const val NAV_CALL_ID = "NAV-Call-id"
    internal const val NAV_CONSUMER_TOKEN = "Nav-Consumer-Token"
}

// misc configuration
internal const val CORRELATION_ID = "correlationId"

internal val ABSOLUTE_FEATURE_PATH = FilePath("features.path").findFolderPath()
