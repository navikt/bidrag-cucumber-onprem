package no.nav.bidrag.cucumber.onprem.organisasjon

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.fnr1
import no.nav.bidrag.cucumber.model.fnr2
import no.nav.bidrag.cucumber.model.fnr3

@Suppress("unused") // used by cucumber
class OrganisasjonEgenskaper : No {
  init {
    Når("jeg henter informasjon om saksbehandler med ident {string}") { ident: String ->
      CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet("/saksbehandler/info/$ident")
    }

    Når("jeg henter enheter for saksbehandler med ident {string}") { ident: String ->
      CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet("/saksbehandler/enhetsliste/$ident")
    }

    Når("jeg henter journalfoerende enheter fra arbeidsfordeling") {
      CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet("/arbeidsfordeling/enhetsliste/journalforende")
    }

    Når("jeg henter enheter fra arbeidsfordeling for person med ident {string}") { ident: String ->
      CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet("/arbeidsfordeling/enhetsliste/geografisktilknytning/$ident")
    }
    Når("jeg henter enhet fra arbeidsfordeling for HentEnhetRequest") {
      CucumberTestRun.hentRestTjenesteTilTesting().exchangePost(
        "/arbeidsfordeling/enhet/geografisktilknytning",
        """{ "ident":"$fnr1", "biidenter":["$fnr2", "$fnr3"] }"""
      )
    }
  }
}