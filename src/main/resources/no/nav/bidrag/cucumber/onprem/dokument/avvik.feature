# language: no
@avviksbehandling
Egenskap: avvik for bidrag-dokument (/journal/*/avvik REST API)

  Tester REST API for avvik i bidrag-dokument.

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument'
    Og nøkkel for testdata 'TEST_AVVIKSBEHANDLING'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
            """
            {
                "batchNavn": "batchen",
                "beskrivelse": "Test avviksbehandling",
                "fagomrade": "BID",
                "feilfortSak": "false",
                "filnavn": "fila",
                "dokumentType": "I",
                "journalstatus": "J",
                "originalBestilt": "false",
                "saksnummer": "0000003",
                "skannetDato": "2019-08-21"
            }
            """

  Scenario: bidrag-dokument: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal kun inneholde:
      | BESTILL_ORIGINAL      |
      | BESTILL_RESKANNING    |
      | BESTILL_SPLITTING     |
      | ENDRE_FAGOMRADE       |
      | INNG_TIL_UTG_DOKUMENT |
      | FEILFORE_SAK          |
      | SEND_TIL_FAGOMRADE    |

  Scenario: bidrag-dokument: Sjekk at man kan bestille original
    Gitt avvikstype 'BESTILL_ORIGINAL'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: bidrag-dokument: Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Gitt avvikstype 'BESTILL_ORIGINAL'
    Når jeg behandler avvik på opprettet journalpost
    Og jeg ber om gyldige avviksvalg for journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal ikke inneholde 'BESTILL_ORIGINAL'

  Scenario: bidrag-dokument: Sjekk at man kan bestille reskannning
    Gitt avvikstype 'BESTILL_RESKANNING'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: bidrag-dokument: Sjekk at man ikke kan bestille ukjent avvik
    Gitt avvikstype 'BLAH_BLAH_LAH_123'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 400

  Scenario: bidrag-dokument: Sjekk at man kan bestille splitting
    Gitt avvikstype 'BESTILL_SPLITTING'
    Og avvikstypen har beskrivelse 'Splitt på midten'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: bidrag-dokument: Sjekk at man kan endre fagområde til FAR
    Gitt avvikstype 'ENDRE_FAGOMRADE'
    Og avviksdetaljer 'fagomrade' = 'FAR'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: bidrag-dokument: Sjekk at endring av fagområde feiler når vi prøver å endre fra FAR til FAR
    Gitt avvikstype 'ENDRE_FAGOMRADE'
    Og avviksdetaljer 'fagomrade' = 'FAR'
    Når jeg behandler avvik på opprettet journalpost
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 400
