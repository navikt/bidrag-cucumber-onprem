# language: no
@bidrag-person
Egenskap: bidrag-person

  Bakgrunn: Felles egenskaper for alle scenario
    Gitt nais applikasjon 'bidrag-person'

  Scenario: bidrag-person: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: bidrag-person: Sjekk at gyldig person-id returnerer OK (200) respons
    Når jeg henter informasjon for ident '27058426518'
    Så skal http status være 200

  Scenario: bidrag-person: Sjekk at ugyldig person-id returnerer NO CONTENT (204) respons
    Når jeg henter informasjon for ident '27067299999'
    Så skal http status være 204
