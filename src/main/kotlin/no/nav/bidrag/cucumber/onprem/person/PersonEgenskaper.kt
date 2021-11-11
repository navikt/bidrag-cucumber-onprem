package no.nav.bidrag.cucumber.onprem.person

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.onprem.FellesEgenskaperService.hentRestTjeneste

@Suppress("unused") // used by cucumber
class PersonEgenskaper: No {
    init {
        Når("jeg henter informasjon for ident {string}") { ident: String ->
            hentRestTjeneste().exchangeGet("/informasjon/$ident")
        }
    }
}
