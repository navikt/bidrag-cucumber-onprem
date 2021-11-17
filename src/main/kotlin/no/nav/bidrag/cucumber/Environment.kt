package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestsModel
import org.slf4j.LoggerFactory

internal object Environment {
    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(Environment::class.java)

    val isSanityCheckFromApplication: Boolean? get() = fetchPropertyOrEnvironment(SANITY_CHECK)?.toBoolean()

    val navAuth: String get() = fetchPropertyOrEnvironment(userAuthPropName()) ?: unknownProperty(userAuthPropName())
    val navUsername: String? get() = fetchPropertyOrEnvironment(NAV_USER) ?: CucumberTestRun.navUsername
    val testUserAuth: String get() = fetchPropertyOrEnvironment(testAuthPropName()) ?: unknownProperty(testAuthPropName())
    val testUsername: String? get() = fetchPropertyOrEnvironment(TEST_USER) ?: CucumberTestRun.testUsername
    val tenantUsername: String get() = "F_${testUsernameUppercase()}.E_${testUsernameUppercase()}@trygdeetaten.no"

    fun fetchPropertyOrEnvironment(key: String): String? = System.getProperty(key) ?: System.getenv(key)
    private fun testAuthPropName() = TEST_AUTH + '_' + testUsernameUppercase()
    private fun testUsernameUppercase() = testUsername?.uppercase()
    private fun userAuthPropName() = "${NAV_AUTH}_${navUsername?.uppercase()}"
    private fun unknownProperty(property: String): String = throw IllegalStateException("Ingen $property å finne!")

    fun initCucumberEnvironment(cucumberTestsModel: CucumberTestsModel) {
        LOGGER.info("Initializing environment for $cucumberTestsModel")
        CucumberTestRun(cucumberTestsModel).initEnvironment()
    }

    /**
     * removes thread specific data values
     */
    fun resetCucumberEnvironment() {
        System.clearProperty(NAV_USER)
        System.clearProperty(NO_CONTEXT_PATH_FOR_APPS)
        System.clearProperty(SANITY_CHECK)
        System.clearProperty(SECURITY_TOKEN)
        System.clearProperty(TAGS)
        System.clearProperty(TEST_USER)
        CucumberTestRun.endRun()
    }

    fun asList(key: String): List<String> {
        return fetchPropertyOrEnvironment(key)?.split(",") ?: emptyList()
    }
}
