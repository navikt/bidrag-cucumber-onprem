# language: no
@sakjournal
Egenskap: bidrag-dokument: applikasjon

  Saksjournalen til bidrag-dokument er klar for bruk

  Bakgrunn: Felles for scenarier
    Gitt nais applikasjon 'bidrag-dokument'

  Scenario: bidrag-dokument: skal kunne hente informasjon om status
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: bidrag-dokument - skal kunne bruke en operasjon med sikkerhet satt opp
    Når det gjøres et kall til '/sak/0000003/journal?fagomrade=BID'
    Så skal http status ikke være 401 eller 403
