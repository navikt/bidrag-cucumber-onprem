# language: no
@bidrag-cucumber-onprem
Egenskap: bidrag-cucumber-onprem

  Tester REST API med swagger i bidrag-cucumber-onprem

  Bakgrunn: Rest-tjeneste.
    Gitt nais applikasjon 'bidrag-cucumber-onprem'

  Scenario: Sjekk at swagger-ui er operativt
    Når det gjøres et kall til '/swagger-ui/index.html?configUrl=/bidrag-cucumber-onprem/v3/api-docs/swagger-config#/'
    Så skal http status være 200
