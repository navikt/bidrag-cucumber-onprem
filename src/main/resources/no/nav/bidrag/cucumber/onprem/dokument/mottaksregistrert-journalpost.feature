# language: no
@bdok-mottaksregistrert
Egenskap: journalposter som har journalstatus mottaksregistrert i bidrag-dokument (/journal/* REST API)

  Tester REST API for journalposter som har journalstatus mottaksregistrert i bidrag-dokument.

  Bakgrunn: Nais-applikasjon for tester som henter journalpost uten sakstilknytning
    Gitt nais applikasjon 'bidrag-dokument'

  Scenario: Hent med ugyldig prefix i journalpost id
    Gitt at jeg henter journalpost med path '/journal/XXX-123'
    Så skal http status være 400

  Scenario: Hent med ukjent journalpost id
    Gitt at jeg henter journalpost med path '/journal/BID-123'
    Så skal http status være 404

  Scenario: Hent en journalpost som har journalstatus mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'MOTTAKSREGISTRERING':
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
    Og at jeg henter opprettet journalpost med nokkel 'MOTTAKSREGISTRERING'
    Så skal http status være 200
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'

  Scenario: Hent en journalpost som ikke har journalstatus mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'JOURNALFØRT':
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
        "journalstatus": "J",
        "saksnummer": "0000003"
        }
        """
    Og at jeg henter opprettet journalpost med nokkel 'JOURNALFØRT'
    Så skal http status være 200
    Og responsen skal ikke inneholde 'journalstatus' = 'J'

  Scenario: Registrer (endre) journalpost som har status mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'REGISTRERING':
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
    Og jeg registrerer endring på opprettet journalpost med nøkkel 'REGISTRERING':
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
    Og at jeg henter endret journalpost for nøkkel 'REGISTRERING'
    Så skal http status være 200
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'M'
    Og en journalpostHendelse for nokkel 'REGISTRERING' skal være produsert

  Scenario: Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'JOURNALFOR':
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
    Og jeg registrerer endring på opprettet journalpost med nøkkel 'JOURNALFOR':
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
    Og at jeg henter endret journalpost for nøkkel 'JOURNALFOR'
    Så skal http status være 200
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
#    Og en journalpostHendelse for nokkel 'JOURNALFOR' skal være produsert

  Scenario: Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt opprettet journalpost på nøkkel 'JOURNALFOR_DOK':
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
    Og jeg registrerer endring på opprettet journalpost med nøkkel 'JOURNALFOR_DOK' og enhet '4806':
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
    Og at jeg henter endret journalpost for nøkkel 'JOURNALFOR_DOK'
    Så skal http status være 200
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalforendeEnhet' = '4806'
