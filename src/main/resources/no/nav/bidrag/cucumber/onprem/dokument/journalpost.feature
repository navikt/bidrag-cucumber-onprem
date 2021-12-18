# language: no
@bdok-journalpost
Egenskap: bidrag-dokument (/sak/*/journal REST API)

  Tester REST API til journalpost endepunktet i bidrag-dokument.

  Bakgrunn: Spesifiser resttjeneste og testdata grunnlag.
    Gitt nais applikasjon 'bidrag-dokument'
    Og nøkkel for testdata 'JOURNALPOSTER_BD'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "journalposter feature",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "journalstatus": "J",
        "gjelder": "29118012345",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "skannetDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """
    Og lag bidragssak '0000003' når den ikke finnes fra før:
            """
            {
              "saksnummer": "0000003",
              "enhetsnummer": "4833"
            }
            """

  Scenario: Sjekk operativt health endpoint
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og responsen skal inneholde 'status' = 'UP'

  @ignored # ignorert i bidrag-cucumber-backend grunnet sikkerhet i q2... fortsetter ignorering
  Scenario: Sjekk at vi får en sakjournal for en sak
    Og jeg henter journalposter for nøkkel 'JOURNALPOSTER_BD' og fagområde
    Så skal http status være 200
    Og så skal responsen være en liste
    Og hvert element i listen skal ha følgende properties satt:
      | fagomrade    |
      | dokumenter   |
      | dokumentDato |

  Scenario: Sjekk at vi får korrekt basisinnhold journalpost for en gitt journalpostId
    Gitt saksnummer '0000003'
    Og jeg henter journalpost som har id for nøkkel 'JOURNALPOSTER_BD'
    Så skal http status være 200
    Og responsen skal inneholde et objekt med navn 'journalpost' som har feltene:
      | avsenderNavn  |
      | dokumentDato  |
      | dokumentType  |
      | journalstatus |
      | journalfortAv |
      | innhold       |

  Scenario: Sjekk at sak som ikke finnes git HttpStatus 404 - Not Found
    Gitt jeg henter journalpost for sak "XYZ" som har id "BID-12345667"
    Så skal http status være 404

  Scenario: Sjekk at dokumentDato kan oppdateres til 2001-01-01
    Gitt jeg endrer journalpost med nøkkel 'JOURNALPOSTER_BD':
            """
            {
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000003",
            "saksnummerSomSkalErstattes":
            "0000003"
            },
            "gjelder": "29118012345",
            "avsenderNavn": "Cucumber Test",
            "beskrivelse": "Bær, Bjarne",
            "journaldato": "2006-05-09",
            "dokumentDato": "2001-01-01"
            }
            """
    Så skal http status være 200
    Og jeg henter journalpost for nøkkel 'JOURNALPOSTER_BD'
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-01-01'

  Scenario: Sjekk at dokumentDato kan oppdateres til 2001-02-01
    Gitt jeg endrer journalpost med nøkkel 'JOURNALPOSTER_BD':
            """
            {
            "journalpostId": 30040789,
            "saksnummer": {
            "erTilknyttetNySak": false,
            "saksnummer": "0000003",
            "saksnummerSomSkalErstattes":
            "0000003"
            },
            "gjelder": "29118012345",
            "avsenderNavn": "Cucumber Test",
            "beskrivelse": "Bær, Bjarne",
            "journaldato": "2006-05-09",
            "dokumentDato": "2001-02-01"
            }
            """
    Så skal http status være 200
    Og jeg henter journalpost for nøkkel 'JOURNALPOSTER_BD'
    Så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'dokumentDato' = '2001-02-01'
