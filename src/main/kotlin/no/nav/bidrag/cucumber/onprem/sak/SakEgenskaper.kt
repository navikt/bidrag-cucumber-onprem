package no.nav.bidrag.cucumber.onprem.sak

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun

@Suppress("unused") // used by cucumber
class SakEgenskaper : No {
    init {
        Når("jeg henter bidragssaker for person med fnr {string}") { fnr: String ->
            CucumberTestRun.hentRestTjenesteTilTesting()
                .exchangeGet("/bidrag-sak/person/sak/$fnr", failOnNotFound = false)
        }
        Når("jeg oppretter bidragssak med enhet {string}") { enhet: String ->
            CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                "/bidrag-sak/sak/ny",
                """{"eierfogd":"$enhet"}""",
                true,
                "X-Enhet" to enhet
            )
        }

        Når("jeg oppretter bidragssak med rolle for fnr {string}") { fnr: String ->
            val body =
                """{ "eierfogd": "2260",
                     "roller": [ { "fodselsnummer": "$fnr", "type": "BP" },
                                 { "fodselsnummer": "12529050370", "type": "BM" },
                                 { "fodselsnummer": "16446030772", "reellMottager": "16446030772", "type": "BA" } ] }"""

            CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                "/sak",
                body,
                true
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
