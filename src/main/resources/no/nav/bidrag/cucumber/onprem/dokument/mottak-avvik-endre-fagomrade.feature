# language: no
@bdok-mot-avvik-endre-fagomrade
Egenskap: Avvikshendelse ENDRE_FAGOMRADE på journalposter som er mottaksregistrert - bidrag-dokument (REST API: /journal/*/avvik?journalstatus=M)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt nais applikasjon 'bidrag-dokument'
    Og nøkkel for testdata 'BDOK_MOT_ENDRE_FAG'
    Og avvikstype 'ENDRE_FAGOMRADE'
    Og opprett journalpost når den ikke finnes:
      """
        {
          "avsenderNavn": "Cucumber Test",
          "beskrivelse": "Test endre fagområde på mottaksregistrert journalpost",
          "dokumentType": "I",
          "fagomrade": "BID",
          "journalforendeEnhet": "4806",
          "journalstatus":"M"
        }
      """

  Scenario: bidrag-dokument - Skal finne avviket ENDRE_FAGOMRADE på mottaksregistrert journalpost
    Når jeg ber om gyldige avviksvalg for mottaksregistrert journalpost
    Så skal listen med avvikstyper inneholde 'ENDRE_FAGOMRADE'

  Scenario: bidrag-dokument - Behandle ENDRE_FAGOMRADE og sjekke endringen av journalpost
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Og avviksdetaljer 'fagomrade' = 'FAR'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200
    Og jeg henter journalpost
    Og skal responsen inneholde et objekt med navn 'journalstatus' som har feltet 'fagomrade' = 'FAR'
