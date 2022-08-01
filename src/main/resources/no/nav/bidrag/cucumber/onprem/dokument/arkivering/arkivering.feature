# language: no
@bidrag-dokument-arkivering
Egenskap: bidrag-dokument-arkivering

  Tester REST API til endepunkt i bidrag-dokument-arkivering.

  Bakgrunn: Tester hent journalpost uten sakstilknytning
    Gitt nais applikasjon 'bidrag-dokument-arkivering'
    Og nøkkel for testdata 'JOURNALPOSTER_BD'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "journalposter feature",
        "journalforendeEnhet": "4833",
        "dokumentType": "U",
        "dokumentdato": "2019-01-01",
        "brevKode": "TEST100",
        "dokumentreferanse": "1234567890",
        "opprettDokument": true,
        "fagomrade": "BID",
        "journalstatus": "KP",
        "journalfortAv": "Cucumber test",
        "gjelder": "27516441319",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "skannetDato": "2019-01-01",
        "saksnummer": "1000003"
        }
        """
    Og lag bidragssak '0000003' når den ikke finnes fra før:
            """
            {
              "saksnummer": "1000003",
              "enhetsnummer": "4833"
            }
            """

  Scenario: Arkivere reservert journalpost
    Når jeg ber om at en bidrag journalpost arkiveres i Joark
    Så skal http status være 200
