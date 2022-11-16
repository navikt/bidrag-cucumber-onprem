package no.nav.bidrag.cucumber.onprem.sak

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun

@Suppress("unused") // used by cucumber
class SakEgenskaper : No {
    init {
        Når("jeg henter bidragssaker for person med fnr {string}") { fnr: String ->
            CucumberTestRun.hentRestTjenesteTilTesting()
                .exchangeGet("/person/sak/$fnr", failOnNotFound = false)
        }
        Når("jeg oppretter bidragssak med enhet {string}") { enhet: String ->
            CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                "/sak/ny",
                """{"eierfogd":"$enhet"}""",
                true,
                "X-Enhet" to enhet
            )
        }

        Og("bruk av en produksjonsbrukeren 'srvbisys' med tilgang til bidrag-sak pip") {
            TODO("Not yet implemented")
        }

        Når("jeg henter pip for sak '9999999'") {
            TODO("Not yet implemented")
        }
    }
}
