# language: no
@bidrag-dokument-arkiv
Egenskap: bidrag-dokument-arkiv

  Tester REST API til endepunkt i bidrag-dokument-arkiv.

  Bakgrunn: REST grensesnitt for nais applikasjon bidrag-dokument-arkiv
    Gitt nais applikasjon 'bidrag-dokument-arkiv'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: bidrag-dokument-arkiv: Sjekk at henting av journal resulterer i ei tom liste (SAF-grensesnitt, testbruker må ha rolle GOSYS_NASJONAL)
    Når jeg kaller endpoint '/sak/1234567/journal' med parameter 'fagomrade' = 'BID'
    Så skal http status være 200
    Og så skal responsen være ei tom liste

#  Scenario: Opprett en journalpost og finn den via SAF query (SAF-grensesnitt, testbruker må ha rolle GOSYS_NASJONAL)
#    Gitt at det opprettes en journalpost i joark med tema BID og saksnummer '1001001'
#    Når jeg kaller endpoint '/sak/1001001/journal' med parameter 'fagomrade' = 'BID'