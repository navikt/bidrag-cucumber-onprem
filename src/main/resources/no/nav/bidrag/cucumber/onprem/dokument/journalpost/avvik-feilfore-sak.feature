# language: no
@avvik-feilfore-sak
Egenskap: avvik bidrag-dokument-journalpost: FEILFORE_SAK

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'AVVIK_FEILFORE_SAK'
    Og avvikstype 'FEILFORE_SAK'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test FEILFORE_SAK",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003",
        "journalstatus": "J"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal inneholde 'FEILFORE_SAK'

  Scenario: Sjekk at man kan feilfore sak
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: Sjekk at avviksvalg for gitt journalpost ikke inneholder FEILFORE_SAK
    Når jeg behandler avvik på opprettet journalpost
    Og jeg henter journalpost
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'feilfort' = 'true'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'S'
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal ikke inneholde 'FEILFORE_SAK'

  Scenario: Sjekk at feilregistrert journalpost blir returnert i journalen og er feilført
    Når jeg behandler avvik på opprettet journalpost
    Og jeg henter sakjournal for opprettede testdata
    Så skal http status være 200
    Og så skal responsen inneholde ei liste med et objekt som har feltet 'feilfort' = true
