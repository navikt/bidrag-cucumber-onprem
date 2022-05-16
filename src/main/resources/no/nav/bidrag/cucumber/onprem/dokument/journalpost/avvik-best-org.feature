# language: no
@avvik-best-org
Egenskap: avvik bidrag-dokument-journalpost: BESTILL_ORIGINAL

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'AVVIK_BESTILL_ORIGINAL'
    Og avvikstype 'BESTILL_ORIGINAL'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test bestill original",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "journalstatus": "J",
        "mottattDato": "2019-01-01",
        "originalBestilt": "false",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost inneholder BESTILL_ORIGINAL
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal inneholde 'BESTILL_ORIGINAL'

  Scenario: Sjekk at man kan bestille original
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Når jeg behandler avvik på opprettet journalpost
    Og jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal ikke inneholde 'BESTILL_ORIGINAL'

  @ignored # sikkerhetstoken (sts) mot dokarkiv-api må settes opp
  Scenario: Sjekk at oppgave blir laget for bestill original
    Når jeg behandler avvik på opprettet journalpost
    Og jeg søker etter oppgaver for mottaksregistrert journalpost
    Så skal http status for oppgavesøket være 200
    Og søkeresultatet skal inneholde 1 oppgave
