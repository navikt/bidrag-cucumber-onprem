package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.logback.TestMessageBeforeLayoutHolder
import no.nav.bidrag.cucumber.model.BidragCucumberSingletons
import no.nav.bidrag.cucumber.model.CucumberTestsModel
import no.nav.bidrag.cucumber.model.TokenType
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService
import no.nav.bidrag.cucumber.service.OidcTokenService
import org.slf4j.LoggerFactory

internal object Environment {
    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(Environment::class.java)

    @JvmStatic
    private val CUCUMBER_TESTS = ThreadLocal<CucumberTestsModel>()

    @JvmStatic
    private val INGRESS_FOR_APP = ThreadLocal<MutableMap<String, String>>()

    private val alleIngresserForApper: String
        get() = fetchPropertyOrEnvironment(INGRESSES_FOR_APPS) ?: CUCUMBER_TESTS.get()?.fetchIngressesForAppsAsString() ?: ""

    val isSanityCheck: Boolean get() = fetchPropertyOrEnvironment(SANITY_CHECK)?.toBoolean() ?: CUCUMBER_TESTS.get()?.sanityCheck ?: false
    val navAuth: String get() = fetchPropertyOrEnvironment(userAuthPropName()) ?: unknownProperty(userAuthPropName())
    val navUsername: String? get() = fetchPropertyOrEnvironment(NAV_USER) ?: CUCUMBER_TESTS.get()?.navUsername
    val testUsername: String? get() = fetchPropertyOrEnvironment(TEST_USER) ?: CUCUMBER_TESTS.get()?.testUsername
    val testUserAuth: String get() = fetchPropertyOrEnvironment(testAuthPropName()) ?: unknownProperty(testAuthPropName())
    val tenantUsername: String get() = "F_${testUsernameUppercase()}.E_${testUsernameUppercase()}@trygdeetaten.no"
    val isFeatureBranch: Boolean get() = CUCUMBER_TESTS.get()?.isFeatureBranch() ?: false
    val isNotSanityCheck: Boolean get() = !isSanityCheck
    val isTestUserPresent: Boolean get() = testUsername != null

    private fun fetchPropertyOrEnvironment(key: String) = System.getProperty(key) ?: System.getenv(key)
    private fun testAuthPropName() = TEST_AUTH + '_' + testUsernameUppercase()
    private fun userAuthPropName() = "${NAV_AUTH}_${navUsername?.uppercase()}"
    private fun testUsernameUppercase() = testUsername?.uppercase()
    private fun unknownProperty(property: String): String = throw IllegalStateException("Ingen $property å finne!")

    fun fetchIngress(applicationName: String): String {
        if (needToFetchIngresses()) {
            fetchIngresses()
        }

        return INGRESS_FOR_APP.get()[applicationName] ?: throw IllegalStateException("Fant ikke ingress for $applicationName!")
    }

    private fun needToFetchIngresses(): Boolean {
        if (INGRESS_FOR_APP.get() == null) {
            INGRESS_FOR_APP.set(HashMap())
            return true
        }

        return INGRESS_FOR_APP.get().isEmpty()
    }

    private fun fetchIngresses() {
        alleIngresserForApper.split(',').forEach { string: String ->
            if (string.contains('@')) {
                val (ingress, app) = splitIngressAndApplication(string)
                INGRESS_FOR_APP.get()[app] = ingress
            } else {
                LOGGER.error("kunne ikke lage ingress av $string")
            }
        }
    }

    private fun splitIngressAndApplication(string: String): Pair<String, String> {
        val ingress = string.split('@')[0]
        val app = string.split('@')[1]
            .replace("no-tag:", "")

        LOGGER.info("Ingress@naisApp: $string")

        return Pair(ingress, app)
    }

    fun initCucumberEnvironment(cucumberTestsModel: CucumberTestsModel) {
        LOGGER.info("Initializing environment for $cucumberTestsModel")
        CUCUMBER_TESTS.set(cucumberTestsModel)
        cucumberTestsModel.warningLogDifferences()
        TestMessageBeforeLayoutHolder.startTestRun()
    }

    /**
     * removes thread specific data values
     */
    fun resetCucumberEnvironment() {
        System.clearProperty(SANITY_CHECK)
        System.clearProperty(SECURITY_TOKEN)
        System.clearProperty(TEST_USER)
        CUCUMBER_TESTS.remove()
        INGRESS_FOR_APP.remove()
        BidragCucumberSingletons.removeRunStats()
        FellesEgenskaperService.fjernResttjenester()
        OidcTokenService.fjernToken()
        RestTjenesteForApplikasjon.removeAll()
        TestMessageBeforeLayoutHolder.endTestRun()
    }

    fun isNoContextPathForApp(applicationName: String) =
        fromPropertyOrEnvironment(applicationName) ?: fromCucumberTestsModel(applicationName) ?: false

    fun hentTokeType() = TokenType.fetch(CUCUMBER_TESTS.get().cucumberTestsApi.tokenType)
    private fun fromPropertyOrEnvironment(applicationName: String) = fetchPropertyOrEnvironment(NO_CONTEXT_PATH_FOR_APPS)?.contains(applicationName)
    private fun fromCucumberTestsModel(applicationName: String) = CUCUMBER_TESTS.get()?.noContextPathForApps?.contains(applicationName)
}
