# language: no
@avvik-slett-jp
Egenskap: avvik bidrag-dokument-journalpost: SLETT_JOURNALPOST

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'AVVIK_SLETT_JP'
    Og avvikstype 'SLETT_JOURNALPOST'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "batchNavn": "En batch",
        "beskrivelse": "Test slett journalpost",
        "dokumentType": "U",
        "journalstatus": "J",
        "fagomrade": "BID",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "journalstatus": "D",
        "mottattDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal inneholde 'SLETT_JOURNALPOST'

  Scenario: Sjekk at jeg kan slette journalpost
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: Sjekk avviksvalg at for gitt journalpost med journalstatus slett journalpost skal være ei tom liste
    Når jeg behandler avvik på opprettet journalpost
    Og jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og så skal responsen være ei tom liste

  Scenario: Sjekk at slettet journalpostid ikke lenger returneres i saksjournalen
    Når jeg behandler avvik på opprettet journalpost
    Og jeg henter sakjournal for opprettede testdata
    Så skal http status være 200
    Og listen med journalposter skal ikke inneholde id for journalposten
