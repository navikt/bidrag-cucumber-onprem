# language: no
@bidrag-organisasjon
Egenskap: bidrag-organisasjon

  Bakgrunn: Felles for hvert scenario
    Gitt nais applikasjon 'bidrag-organisasjon'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at gyldig saksbehandler-id returnerer OK (200) respons
    Når jeg henter informasjon om saksbehandler med ident 'H153959'
    Så skal http status være 200

  Scenario: Sjekk at hent av enheter for saksbehandler-id returnerer OK (200) respons
    Når jeg henter enheter for saksbehandler med ident 'Z992022'
    Så skal http status være 200

  Scenario: Sjekk at hent av journalfoerende enheter fra arbeidsfordeling returnerer OK (200) respons
    Når jeg henter journalfoerende enheter fra arbeidsfordeling
    Så skal http status være 200

  Scenario: Sjekk at hent av enheter fra arbeidsfordeling for person-id returnerer OK (200) respons
    Når jeg henter enheter fra arbeidsfordeling for person med ident '29068918861'
    Så skal http status være 200

  Scenario: Sjekk at hent av enheter fra arbeidsfordeling for HentEnhetRequest returnerer OK (200) respons
    Når jeg henter enhet fra arbeidsfordeling for HentEnhetRequest
    Så skal http status være 200
