# language: no
@bidrag-person
Egenskap: bidrag-person

  Bakgrunn: Felles egenskaper for alle scenario
    Gitt nais applikasjon 'bidrag-person'

  Scenario: Sjekk at swagger-ui er operativt
    Når det gjøres et kall til '/swagger-ui/index.html?configUrl=/bidrag-person/v3/api-docs/swagger-config#/'
    Så skal http status være 200

  Scenario: bidrag-person: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: bidrag-person: Sjekk at gyldig person-id returnerer OK (200) respons
    Når jeg henter informasjon for ident '29068918861'
    Så skal http status være 200

  Scenario: bidrag-person: Sjekk at ugyldig person-id returnerer NO CONTENT (204) respons
    Når jeg henter informasjon for ident '27067299999'
    Så skal http status være 204

  @ignored #  Feiler i PDL, kommenter inn testen når dette blir fikset.
  Scenario: Endepunkt for henting av alle personer i samme husstand fungerer som det skal
    Når vi henter alle personer som bor i samme husstand som angitt person
    Så skal http status være 200

  Scenario: Endepunkt for henting av fødselsdatoer fungerer som det skal
    Når vi henter fødselsdatoer for en liste med personer
    Så skal http status være 200

  Scenario: Endepunkt for henting av graderingsinfo fungerer som det skal
    Når vi henter graderingsinfo for en liste med personer
    Så skal http status være 200

  Scenario: Endepunkt for henting av geografisk tilknytning fungerer som det skal
    Når vi henter informasjon om geografisk tilknytning for en person
    Så skal http status være 200

  Scenario: Endepunkt for henting av alle forelder/barn-relasjoner fungerer som det skal
    Når vi henter alle forelder barn relasjoner for en person
    Så skal http status være 200

  Scenario: Endepunkt for henting av informasjon om persons navn, fødselsdata og eventuell død fungerer som det skal
    Når vi henter informasjon om en persons navn, fødselsdata og eventuell død
    Så skal http status være 200

  Scenario: Endepunkt for henting av sivilstand fungerer som det skal
    Når vi henter sivilstand for en person
    Så skal http status være 200
