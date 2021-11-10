package no.nav.bidrag.cucumber.onprem

import io.cucumber.java8.No
import io.cucumber.java8.Scenario
import no.nav.bidrag.cucumber.ScenarioManager

@Suppress("unused") // used by cucumber
class BidragCucumberOnpremHooks : No {
    init {
        Before(10) { scenario: Scenario ->
            ScenarioManager.use(scenario)
        }

        After(10) { scenario: Scenario ->
            ScenarioManager.reset(scenario)
        }
    }
}