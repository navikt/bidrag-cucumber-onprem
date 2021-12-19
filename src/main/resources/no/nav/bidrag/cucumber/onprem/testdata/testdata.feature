# language: no
@bidrag-testdata
Egenskap: bidrag-testdata

  Tester REST API til endepunkt i bidrag-testdata.

  Scenario: Sjekk at health endpoint er operativt
    Gitt nais applikasjon 'bidrag-testdata'
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og responsen skal inneholde 'status' = 'UP'
