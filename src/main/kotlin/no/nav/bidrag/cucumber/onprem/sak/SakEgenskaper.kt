package no.nav.bidrag.cucumber.onprem.sak

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.fnr1
import no.nav.bidrag.cucumber.model.fnr2
import no.nav.bidrag.cucumber.model.fnr3
import java.time.Year

@Suppress("unused") // used by cucumber
class SakEgenskaper : No {
    init {
        Når("jeg bruker post for å hente bidragssaker for person med fnr som finnes") {
            CucumberTestRun.hentRestTjenesteTilTesting()
                .exchangePost("/person/sak", "\"$fnr1\"")
        }
        Når("jeg oppretter bidragssak med enhet {string}") { enhet: String ->
            CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                "/bidrag-sak/sak/ny",
                """ { "eierfogd":"$enhet" }""",
                true,
                "X-Enhet" to enhet
            )
        }

        Når("jeg oppretter bidragssak med rolle for fnr som finnes") {
            val body =
                """ {
                        "eierfogd": "2260",
                        "roller": [ { "fodselsnummer": "$fnr3", "type": "BP" },
                        { "fodselsnummer": "$fnr2", "type": "BM" },
                        { "fodselsnummer": "$fnr1", "reellMottager": "$fnr1", "type": "BA" } ]
                    }"""

            CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                "/sak",
                body,
                true
            )
        }

        Når("jeg oppdaterer en bidragssak") {
            val saksnummer = "${Year.now().value % 100}00001"
            val body =
                """ {
                        "saksnummer":"$saksnummer",
                        "kategorikode":"U",
                        "landkode":"DEU",
                        "konvensjonskode":"HiS",
                        "konvensjonsdato":"2023-01-01",
                        "ffuReferansenr":"654987312"
                    }"""

            CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
                "/sak/oppdater",
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
