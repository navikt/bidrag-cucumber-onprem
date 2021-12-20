# language: no
@bdj-mottaksregistrert
Egenskap: journalposter som har journalstatus mottaksregistrert i bidrag-dokument-journalpost (/journal/* REST API)

  Tester REST API for journalposter som har journalstatus mottaksregistrert i bidrag-dokument.

  Bakgrunn: Nais-applikasjon for tester som henter journalpost uten sakstilknytning
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og lag bidragssak '0000004' når den ikke finnes fra før:
      """
        {
          "saksnummer": "0000004",
          "enhetsnummer": "4806"
        }
      """

  Scenario: bidrag-dokument-journalpost - Hent med ugyldig prefix i journalpost id
    Gitt at jeg henter journalpost med path '/journal/XXX-123'
    Så skal http status være 400

  Scenario: bidrag-dokument-journalpost - Hent med ukjent journalpost id
    Gitt at jeg henter journalpost med path '/journal/BID-123'
    Så skal http status være 404

  Scenario: bidrag-dokument-journalpost - Hent en journalpost som har journalstatus mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'BDJ_MOTTAKSREGISTRERING':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Testdata for test av journalpost med journalstatus 'M'",
        "dokumentType": "I",
        "dokumentdato": "2020-02-02",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "06127412345",
        "journaldato": "2020-02-02",
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "journalstatus": "M"
        }
        """
    Og at jeg henter opprettet journalpost med nøkkel 'BDJ_MOTTAKSREGISTRERING'
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'

  Scenario: bidrag-dokument-journalpost - Hent en journalpost som ikke har journalstatus mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'JOURNALFØRT_BDJ':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Testdata for test av journalpost med journalstatus 'M'",
        "dokumentType": "I",
        "dokumentdato": "2020-02-02",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "06127412345",
        "journaldato": "2020-02-02",
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "saksnummer" : "0000004",
        "journalstatus": "J"
        }
        """
    Og at jeg henter opprettet journalpost med nøkkel 'JOURNALFØRT_BDJ'
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'

  Scenario: bidrag-dokument-journalpost - Registrer (endre) journalpost som har status mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'REGISTRERING_BDJ':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Testdata for test av journalpost med journalstatus 'M'",
        "dokumentType": "I",
        "dokumentdato": "2020-02-02",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "06127412345",
        "journaldato": "2020-02-02",
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "journalstatus": "M"
        }
        """
    Og jeg registrerer endring på opprettet journalpost med nøkkel 'REGISTRERING_BDJ':
      """
      {
        "skalJournalfores":false,
        "gjelder": "01117712345",
        "tittel":"journalfør",
        "endreDokumenter": [
          {
            "brevkode": "SVADA",
            "dokId": 0,
            "tittel": "journalfør"
          }
        ]
      }
      """
    Så skal http status være 200
    Og at jeg henter endret journalpost for nøkkel 'REGISTRERING_BDJ'
    Så skal http status være 200
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'
    Og en journalpostHendelse for nøkkel 'REGISTRERING_BDJ' skal være produsert

  Scenario: bidrag-dokument-journalpost - Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'JOURNALFOR_BDJ':
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Testdata for test av journalpost med journalstatus 'M'",
        "dokumentType": "I",
        "dokumentdato": "2020-02-02",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "gjelder": "06127412345",
        "journaldato": "2020-02-02",
        "mottattDato": "2020-02-02",
        "skannetDato": "2020-02-02",
        "journalstatus": "M"
        }
        """
    Og jeg registrerer endring på opprettet journalpost med nøkkel 'JOURNALFOR_BDJ':
      """
      {
        "skalJournalfores":true,
        "gjelder": "01117712345",
        "tittel":"journalfør",
        "tilknyttSaker":["0000004"],
        "endreDokumenter": [
          {
            "brevkode": "SVADA",
            "dokId": 0,
            "tittel": "journalfør"
          }
        ]
      }
      """
    Så skal http status være 200
    Og at jeg henter endret journalpost for nøkkel 'JOURNALFOR_BDJ'
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
