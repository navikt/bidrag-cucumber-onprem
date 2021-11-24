package no.nav.bidrag.cucumber.onprem.person

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting

@Suppress("unused") // used by cucumber
class PersonEgenskaper: No {
    init {
        Når("jeg henter informasjon for ident {string}") { ident: String ->
            hentRestTjenesteTilTesting().exchangeGet("/informasjon/$ident")
        }
    }
}
