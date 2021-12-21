# language: no
@bdj-sakjournal
Egenskap: bidrag-dokument-journalpost (/sak/(saksnummer)/journal REST API)

  Bakgrunn: Spesifiser base-url til tjenesten her så vi slipper å gjenta for hvert scenario.
  Fasit environment er gitt ved environment variabler ved oppstart.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og opprettet journalpost på nøkkel 'SAKJOURNAL':
      """
        {
          "avsenderNavn": "Cucumber Test",
          "beskrivelse": "Test endre fagområde",
          "dokumentType": "I",
          "dokumentdato": "2019-01-01",
          "dokumentreferanse": "1234567890",
          "fagomrade": "FAR",
          "gjelder": "29118012345",
          "journalforendeEnhet": "4833",
          "journaldato": "2019-01-01",
          "journalstatus": "J",
          "mottattDato": "2019-01-01",
          "saksnummer": "0603479",
          "skannetDato": "2019-01-01"
        }
      """
    Og lag bidragssak '0603479' når den ikke finnes fra før:
      """
        {
          "saksnummer": "0603479",
          "enhetsnummer": "4806"
        }
      """

  Scenario: Sjekk at vi får en liste med journalposter for en gitt sak
    Når jeg henter journalposter for nøkkel 'SAKJOURNAL' og fagområde 'BID'
    Så skal http status være 200
    Og så skal responsen være ei liste

  Scenario: Sjekk at vi får en journalpost for et farskap på gitt sak
    Når jeg henter journalposter for nøkkel 'SAKJOURNAL' og fagområde 'FAR'
    Så skal http status være 200
    Og så skal responsen være ei liste med innhold
    Og hver journal i listen skal ha 'fagomrade' = 'FAR'

  Scenario: Sjekk at vi får gjelderAktor i journalpost for et farskap på gitt sak
    Når jeg henter journalposter for nøkkel 'SAKJOURNAL' og fagområde 'FAR'
    Så skal http status være 200
    Og så skal responsen være ei liste med innhold
    Og hver journal i listen skal ha objektet 'gjelderAktor' med feltene
      | ident |
