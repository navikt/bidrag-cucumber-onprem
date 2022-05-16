# language: no
@arkiv-joarkhendelse
Egenskap: bidrag-dokument-arkiv: joarkhendelse

  Tester REST API til endepunkt i bidrag-dokument-arkiv.

  Bakgrunn: REST grensesnitt for nais applikasjon bidrag-dokument-arkiv
    Gitt nais applikasjon 'bidrag-dokument-arkiv'

  Scenario: bidrag-dokument-arkiv - Skal sende melding til bidrag-arbeidsflyt når journalpost opprettet
    Gitt fagområdet 'BID'
    Og opprettet joark journalpost på nøkkel 'JOARK_INNGAAENDE_JP':
          """
            {
              "tittel": "Bidrag automatisk test av registrer journalpost",
              "journalposttype": "INNGAAENDE",
              "tema": "BID",
              "kanal": "NAV_NO",
              "journalfoerendeEnhet": "9999",
              "avsenderMottaker": {
                "id": "15277049616",
                "idType": "FNR",
                "navn": "Blund, Jon"
              },
              "bruker": {
                "id": "15277049616",
                "idType": "FNR"
              },
              "sak": {
                "fagsakId": "2121212",
                "sakstype": "FAGSAK",
                "fagsaksystem": "BISYS"
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
    Og jeg registrerer endring på opprettet journalpost med nøkkel 'JOARK_INNGAAENDE_JP':
      """
      {
        "skalJournalfores":true,
        "gjelder": "26447512741",
        "tittel":"Journalfør cucumber test",
        "tilknyttSaker":["0000004"]
      }
      """
    Så skal http status være 200
    Og skal ha totalt 0 åpne journalføringsoppgaver

  Scenario: bidrag-dokument-arkiv - Skal opprette journalføringsoppgave når mottatt journalpost opprettet
    Gitt fagområdet 'BID'
    Og opprettet joark journalpost på nøkkel 'JOARK_INNGAAENDE_JP_2':
          """
            {
              "tittel": "Bidrag automatisk test av registrer journalpost",
              "journalposttype": "INNGAAENDE",
              "tema": "BID",
              "behandlingstema": "ab0322",
              "kanal": "NAV_NO",
              "journalfoerendeEnhet": "0701",
              "avsenderMottaker": {
                "id": "15277049616",
                "idType": "FNR",
                "navn": "Blund, Jon"
              },
              "bruker": {
                "id": "15277049616",
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
    Og skal responsen fra oppgave inneholde feltet 'tildeltEnhetsnr' = '4806'
    Og skal responsen fra oppgave inneholde feltet 'aktoerId' = '2448326340873'
