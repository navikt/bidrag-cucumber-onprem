# language: no
@bidrag-sak
Egenskap: bidrag-sak

  Tester REST API til endepunktet BidragSakController i bidrag-sak.

  Bakgrunn:
    Gitt nais applikasjon 'bidrag-sak'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: Sjekk at vi får NOT FOUND dersom vi ber om sak for person som ikke eksisterer i databasen
    Når jeg henter bidragssaker for person med fnr '12345678901'
    Så skal http status være 404

  @ignored
  @pip
  Scenario: Skal gi 200 for sak 9999999
    Og bruk av en produksjonsbrukeren 'srvbisys' med tilgang til bidrag-sak pip
    Når jeg henter pip for sak '9999999'
    Så skal http status være 200
