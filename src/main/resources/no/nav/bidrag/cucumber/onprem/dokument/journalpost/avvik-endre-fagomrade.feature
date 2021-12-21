# language: no
@avvik-endre-fagomrade
Egenskap: avvik bidrag-dokument-journalpost: ENDRE_FAGOMRADE

  Bakgrunn: Opprett og cache journapostId og sett felles params så vi slipper å gjenta for hvert scenario.
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'AVVIK_ENDRE_FAG'
    Og avvikstype 'ENDRE_FAGOMRADE'
    Og avviksdetaljer 'enhetsnummer' = '4806'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn": "Cucumber Test",
        "beskrivelse": "Test endre fagområde",
        "dokumentType": "I",
        "dokumentdato": "2019-01-01",
        "dokumentreferanse": "1234567890",
        "fagomrade": "BID",
        "journalstatus": "J",
        "gjelder": "29118012345",
        "journalforendeEnhet": "4833",
        "journaldato": "2019-01-01",
        "mottattDato": "2019-01-01",
        "skannetDato": "2019-01-01",
        "saksnummer": "0000003"
        }
        """

  Scenario: Sjekk avviksvalg for gitt journalpost
    Når jeg ber om gyldige avviksvalg for opprettet journalpost
    Så skal http status være 200
    Og listen med avvikstyper skal inneholde 'ENDRE_FAGOMRADE'

  Scenario: Sjekk at endring av fagområde feiler når vi prøver å endre fra FAR til FAR
    Gitt avviksdetaljer 'fagomrade' = 'FAR'
    Når jeg behandler avvik på opprettet journalpost
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 400

  Scenario: Sjekk at jeg kan endre fagområde til FAR og tilbake til BID
    Gitt avviksdetaljer 'fagomrade' = 'FAR'
    Når jeg behandler avvik på opprettet journalpost
    Gitt avviksdetaljer 'fagomrade' = 'BID'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200

  Scenario: Sjekk at når man endrer fagområde til annet enn BID/FAR, så skal den være feilført
    Gitt avviksdetaljer 'fagomrade' = 'NYTT_FAGOMRADE'
    Og avviksdetaljer 'bekreftetSendtScanning' = 'true'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200
    Og jeg henter journalpost
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'slettet' = 'true'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AF'
