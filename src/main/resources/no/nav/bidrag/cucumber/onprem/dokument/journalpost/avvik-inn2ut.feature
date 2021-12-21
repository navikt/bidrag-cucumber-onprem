# language: no
@avvik-inn2ut
Egenskap: avvik bidrag-dokument-journalpost: INNG_TIL_UTG_DOKUMENT

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'AVVIK_INN2UT'
    Og avvikstype 'INNG_TIL_UTG_DOKUMENT'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "batchNavn": "En batch",
        "beskrivelse": "Test inn til utgående",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "filnavn": "svada.pdf",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "journalstatus": "J",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01",
        "filnavn": "svada.pdf"
        }
        """
  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal inneholde 'INNG_TIL_UTG_DOKUMENT'

  Scenario: Sjekk at jeg kan opprette avvik inngående til utgående
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: Sjekk at riktig enhet blir journalført
    Gitt jeg henter journalpost
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalforendeEnhet' = '4806'
