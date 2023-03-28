package no.nav.bidrag.cucumber.onprem.person

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun.Companion.hentRestTjenesteTilTesting
import no.nav.bidrag.cucumber.model.fnr1
import no.nav.bidrag.cucumber.model.fnr2
import no.nav.bidrag.cucumber.model.fnr3

@Suppress("unused") // used by cucumber
class PersonEgenskaper : No {
  init {
    Når("jeg henter informasjon for ident {string}") { ident: String ->
      hentRestTjenesteTilTesting().exchangeGet("/informasjon/$ident")
    }
    Når("jeg henter geografisktilknytning for en ident som finnes") {
      hentRestTjenesteTilTesting().exchangeGet("/geografisktilknytning/$fnr2")
    }
    Når("jeg henter husstandsmedlemmer for en ident som finnes") {
      hentRestTjenesteTilTesting().exchangeGet("/husstandsmedlemmer/$fnr3")
    }
    Når("vi henter fødselsdatoer for en liste med personer") {
      val body = """["$fnr1", "$fnr2", "$fnr3"]"""
      hentRestTjenesteTilTesting().exchangePost("/fodselsdatoer", body)
    }
    Når("vi henter graderingsinfo for en liste med personer") {
      val body = """["$fnr1", "$fnr2", "$fnr3"]"""
      hentRestTjenesteTilTesting().exchangePost("/graderingsinfo", body)
    }
    Når("vi henter informasjon om geografisk tilknytning for en person") {
      val body = """{"ident":"$fnr1"}"""
      hentRestTjenesteTilTesting().exchangePost("/geografisktilknytning", body)
    }
    Når("vi henter sivilstand for en person") {
      val body = """{"ident":"$fnr1"}"""
      hentRestTjenesteTilTesting().exchangePost("/sivilstand", body)
    }
    Når("vi henter alle forelder barn relasjoner for en person") {
      val body = """{"ident":"$fnr1"}"""
      hentRestTjenesteTilTesting().exchangePost("/forelderbarnrelasjon", body)
    }
    Når("vi henter informasjon om en persons navn, fødselsdata og eventuell død") {
      val body = """{"ident":"$fnr1"}"""
      hentRestTjenesteTilTesting().exchangePost("/navnfoedseldoed", body)
    }
    Når("vi henter alle personer som bor i samme husstand som angitt person") {
      val body = """{"ident":"$fnr1"}"""
      hentRestTjenesteTilTesting().exchangePost("/husstandsmedlemmer", body)
    }
  }
}
