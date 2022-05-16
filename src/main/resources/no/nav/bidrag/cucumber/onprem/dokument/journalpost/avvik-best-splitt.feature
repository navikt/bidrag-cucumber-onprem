# language: no
@avvik-best-splitt
Egenskap: avvik bidrag-dokument-journalpost: BESTILL_SPLITTING

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'AVVIK_BEST_SPLITT'
    Og avvikstype 'BESTILL_SPLITTING'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "batchNavn": "En batch",
        "beskrivelse": "Test bestill splitting",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "journalstatus": "J",
        "filnavn": "svada.pdf",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01",
        "filnavn": "svada.pdf"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal inneholde 'BESTILL_SPLITTING'

  Scenario: Sjekk at jeg kan bestille splitting
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Og avvikstypen har beskrivelse 'splitt midt på'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200
    Og jeg henter journalpost
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'feilfort' = 'true'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AS'

  @ignored # sikkerhetstoken (sts) mot dokarkiv-api må settes opp
  Scenario: Sjekk at oppgave blir laget for splitting
    Når jeg behandler avvik på opprettet journalpost
    Og jeg søker etter oppgaver for mottaksregistrert journalpost
    Så skal http status for oppgavesøket være 200
    Og søkeresultatet skal inneholde 1 oppgave
