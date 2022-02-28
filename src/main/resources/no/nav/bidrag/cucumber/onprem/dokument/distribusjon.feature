# language: no
@bidrag-distribusjon
Egenskap: bidrag-distribusjon

  Tester distribusjon av utgående journalposter.

  Bakgrunn: Tester arkiver og distribuer bidrag journalpost i Joark
    Gitt nais applikasjon 'bidrag-dokument-arkivering'
    Og nøkkel for testdata 'JOURNALPOSTER_BD'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Bidrag journalpost cucumber test",
        "journalforendeEnhet": "4833",
        "dokumentType": "U",
        "dokumentdato": "2019-01-01",
        "journaldato": "2019-01-01",
        "brevKode": "BI01A01",
        "dokumentreferanse": "1234567890",
        "opprettDokument": true,
        "fagomrade": "BID",
        "journalstatus": "KP",
        "journalfortAv": "Cucumber test",
        "gjelder": "11126222671",
        "saksnummer": "1000003"
        }
        """
    Og lag bidragssak '10000003' når den ikke finnes fra før:
            """
            {
              "saksnummer": "1000003",
              "enhetsnummer": "4833"
            }
            """

  Scenario: Arkiver og distribuer journalpost
    Når jeg ber om at en bidrag journalpost arkiveres i Joark
    Så skal http status være 200
    Gitt nais applikasjon 'bidrag-dokument'
    Og bestiller distribusjon av Joark journalpost
    Så skal http status være 200
