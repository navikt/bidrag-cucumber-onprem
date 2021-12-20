# language: no
@bidrag-dokument
Egenskap: bidrag-dokument (/tilgang REST API)

  Tester tilgang URL

  @ignored # test i bidrag-cucumber-backend bruker fasit for å verfisere resultat... dette må skrives om...
  Scenario: Sjekk at vi får en gyldig URL for dokument tilgang for saksbehandler
    Gitt nais applikasjon 'bidrag-dokument'
    Når jeg ber om tilgang til dokument på journalpostId '30040789' og dokumentreferanse 'abcdef'
    Så skal http status være 200
    Og dokument url skal være gyldig
