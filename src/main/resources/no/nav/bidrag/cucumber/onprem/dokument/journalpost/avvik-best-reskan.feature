# language: no
@avvik-best-reskan
Egenskap: avvik bidrag-dokument-journalpost: BESTILL_RESKANNING

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'AVVIK_BESTILL_RESKAN'
    Og avvikstype 'BESTILL_RESKANNING'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test reskanning",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "string",
        "fagomrade": "BID",
        "journalstatus": "J",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003",
        "skannetDato": "2019-01-01"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost inneholder BESTILL_RESKANNING
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal inneholde 'BESTILL_RESKANNING'

  Scenario: Sjekk at reskanning kan bestilles
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  @ignored # sikkerhetstoken (sts) mot dokarkiv-api må settes opp
  Scenario: Sjekk at oppgave blir laget for reskanning
    Når jeg behandler avvik på opprettet journalpost
    Og jeg søker etter oppgaver for mottaksregistrert journalpost
    Så skal http status være 200
    Og søkeresultatet skal inneholde 1 oppgave

  Scenario: Sjekk at når man bestiller reskanning, så skal journalposten bli feilført
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200
    Og jeg henter journalpost
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'feilfort' = 'true'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AR'
