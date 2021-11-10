# language: no
@arkiv-swagger
Egenskap: bidrag-dokument-arkiv: swagger

  Tester REST API med swagger i bidrag-dokument-arkiv

  Bakgrunn: Rest-tjeneste.
    Gitt nais applikasjon 'bidrag-dokument-arkiv'

  Scenario: Sjekk at swagger-ui er operativt
    Når det gjøres et kall til '/swagger-ui/index.html?configUrl=/bidrag-dokument-arkiv/v3/api-docs/swagger-config#/'
    Så skal http status være 200
