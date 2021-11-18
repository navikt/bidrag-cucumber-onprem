package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.CucumberTestsModel
import org.slf4j.LoggerFactory

/**
 * Values gathered by environment variables or which is possible to override with system properties or environment variables
 */
internal object Environment {
    val scope: String get() = CucumberTestRun.fetchQenvironmentFromIngress()
    val withSecurityToken: Boolean get() = fetchPropertyOrEnvironment(SECURITY_TOKEN) != null

    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(Environment::class.java)

    private val navUsername: String? get() = fetchPropertyOrEnvironment(NAV_USER) ?: CucumberTestRun.navUsername
    private val testUsername: String? get() = fetchPropertyOrEnvironment(TEST_USER) ?: CucumberTestRun.testUsername

    val isSanityCheck: Boolean? get() = fetchPropertyOrEnvironment(SANITY_CHECK)?.toBoolean()

    val navAuth: String get() = fetchPropertyOrEnvironment(navAuthPropName()) ?: throw unknownProperty(navAuthPropName())
    val securityToken: String? get() = fetchPropertyOrEnvironment(SECURITY_TOKEN)
    val testUserAuth: String get() = fetchPropertyOrEnvironment(testAuthPropName()) ?: throw unknownProperty(testAuthPropName())

    fun fetchPropertyOrEnvironment(key: String): String? = System.getProperty(key) ?: System.getenv(key)
    private fun navAuthPropName() = NAV_AUTH + '_' + navUsername?.uppercase()
    private fun testAuthPropName() = TEST_AUTH + '_' + testUsername?.uppercase()

    private fun unknownProperty(property: String) = IllegalStateException("Ingen $property å finne!")

    fun initCucumberEnvironment(cucumberTestsModel: CucumberTestsModel) {
        LOGGER.info("Initializing environment for $cucumberTestsModel")
        CucumberTestRun(cucumberTestsModel).initEnvironment()
    }

    /**
     * removes properties and thread specific data values
     */
    fun reset() {
        System.clearProperty(NAV_USER)
        System.clearProperty(SANITY_CHECK)
        System.clearProperty(SECURITY_TOKEN)
        System.clearProperty(TEST_USER)
        CucumberTestRun.endRun()
    }

    fun asList(key: String): List<String> {
        return fetchPropertyOrEnvironment(key)?.split(",") ?: emptyList()
    }
}
