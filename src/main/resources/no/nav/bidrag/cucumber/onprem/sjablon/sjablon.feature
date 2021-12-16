# language: no
@bidrag-sjablon
Egenskap: bidrag-sjablon

  Tester REST API til endepunkt i bidrag-sjablon.
  URLer til tjenester hentes via fasit.adeo.no og gjøres ved å spesifisere
  alias til en RestService record i fasit for et gitt miljø.

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt nais applikasjon 'bidrag-sjablon'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'