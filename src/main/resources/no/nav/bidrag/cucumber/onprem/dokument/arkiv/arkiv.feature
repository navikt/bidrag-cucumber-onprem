# language: no
@bidrag-dokument-arkiv
Egenskap: bidrag-dokument-arkiv

  Tester REST API til endepunkt i bidrag-dokument-arkiv.

  Bakgrunn: REST grensesnitt for nais applikasjon bidrag-dokument-arkiv
    Gitt nais applikasjon 'bidrag-dokument-arkiv'

  Scenario: Sjekk at health endpoint er operativt
    Når jeg kaller helsetjenesten
    Så skal http status være 200
    Og header 'content-type' skal være 'application/json'
    Og responsen skal inneholde 'status' = 'UP'

  Scenario: bidrag-dokument-arkiv: Sjekk at henting av journal resulterer i ei tom liste (SAF-grensesnitt, testbruker må ha rolle GOSYS_NASJONAL)
    Når jeg kaller endpoint '/sak/1234567/journal' med parameter 'fagomrade' = 'BID'
    Så skal http status være 200
    Og så skal responsen være ei tom liste

  Scenario: bidrag-dokument-arkiv: Sjekk at distribusjon av journalpost går OK
    Gitt saksnummer '1000000' og fagområdet 'BID'
    Og at det finnes en utgående journalpost i arkiv på fagområdet og saksnummer
    Og kaller journalpost kan arkiveres endepunkt
    Så skal http status være 200
    Og bestiller distribusjon av Joark journalpost
    Så skal http status være 200

  Scenario: bidrag-dokument-arkiv - Registrer (journalfør) journalpost som har status mottaksregistrert
    Gitt fagområdet 'BID'
    Gitt alle søknadsoppgaver med saksnummer '2121212' er lukket
    Og opprettet joark journalpost på nøkkel 'JOARK_INNGAAENDE_JOURNALFOR':
          """
            {
              "tittel": "Bidrag automatisk test av registrer journalpost",
              "journalposttype": "INNGAAENDE",
              "tema": "BID",
              "behandlingstema": "ab0322",
              "kanal": "NAV_NO",
              "journalfoerendeEnhet": "0701",
              "avsenderMottaker": {
                "id": "02459032730",
                "idType": "FNR",
                "navn": "Blund, Jon"
              },
              "bruker": {
                "id": "02459032730",
                "idType": "FNR"
              },
              "dokumenter": [
                {
                  "tittel": "En cucumber test",
                  "brevkode": "NAV 04-01.04",
                  "dokumentvarianter": [
                    {
                      "filtype": "PDFA",
                      "fysiskDokument": "U8O4a25hZCBvbSBkYWdwZW5nZXIgdmVkIHBlcm1pdHRlcmluZw==",
                      "variantformat": "ARKIV"
                    }
                  ]
                }
              ]
            }
          """
    Og skal ha totalt 1 åpne journalføringsoppgaver
    Og skal responsen fra oppgave med type 'JFR' inneholde feltet 'tildeltEnhetsnr' = '4806'
    Og skal responsen fra oppgave med type 'JFR' inneholde feltet 'aktoerId' = '2586584223271'
    Og jeg registrerer endring på opprettet journalpost på enhet '4806' med nøkkel 'JOARK_INNGAAENDE_JOURNALFOR':
      """
      {
        "skalJournalfores":true,
        "gjelder": "19466334734",
        "tittel":"Journalfør cucumber test",
        "tilknyttSaker":["2121212"]
      }
      """
    Så skal http status være 200
    Og skal ha totalt 0 åpne journalføringsoppgaver
    Og skal ha totalt 1 åpne søknadsoppgaver
    Og skal responsen fra oppgave med type 'BEH_SAK' inneholde feltet 'tildeltEnhetsnr' = '4806'
    Og at jeg henter endret journalpost for nøkkel 'JOARK_INNGAAENDE_JOURNALFOR'
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'gjelderAktor.ident' = '19466334734'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'J'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'tema' = 'BID'