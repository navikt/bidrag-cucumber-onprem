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
    Når jeg henter bidragssaker for person med fnr som ikke finnes
    Så skal http status være 404

  Scenario: Sjekk at vi får 201 CREATE hvis vi ber om å opprette en sak i databasen
    Når jeg oppretter bidragssak med enhet '1701'
    Så skal http status være 201

  Scenario: Sjekk at vi får 200 OK når vi ber om å opprette en sak i databasen
    Når jeg oppretter bidragssak med rolle for fnr som finnes
    Så skal http status være 200

  Scenario: Sjekk at vi får 200 OK når vi ber om sak for person som eksisterer i databasen
    Når jeg henter bidragssaker for person med fnr som finnes
    Så skal http status være 200

  Scenario: Sjekk at vi får 200 OK når vi bruker post for å finne sak for person som eksisterer i databasen
    Når jeg bruker post for å hente bidragssaker for person med fnr som finnes
    Så skal http status være 200

  Scenario: Sjekk at vi får 200 når vi oppdaterer en sak som eksisterer i basen
    Når jeg oppdaterer en bidragssak
    Så skal http status være 200

  @ignored
  @pip
  Scenario: Skal gi 200 for sak 9999999
    Og bruk av en produksjonsbrukeren 'srvbisys' med tilgang til bidrag-sak pip
    Når jeg henter pip for sak '9999999'
    Så skal http status være 200
