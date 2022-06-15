# language: no
@bdj-mot-avvik-best-org
Egenskap: Avvikshendelse BESTILL_ORIGINAL på journalposter som er mottaksregistrert - bidrag-dokument-journalpost (REST API: /journal/*/avvik?journalstatus=M)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'BDJ_MOT_BEST_ORG'
    Og avvikstype 'BESTILL_ORIGINAL'
    Og opprett journalpost når den ikke finnes:
      """
        {
          "avsenderNavn"       : "Cucumber Test",
          "batchNavn"          : "En batch",
          "beskrivelse"        : "Test bestill reskanning på mottaksregistrert journalpost",
          "dokumentType"       : "I",
          "journalforendeEnhet": "4806",
          "journalstatus"      : "M",
          "originalBestilt"    : "false",
          "skannetDato"        : "2019-01-01"
        }
      """

  Scenario: bidrag-dokument-journalpost - Skal finne avviket BESTILL_ORIGINAL på mottaksregistrert journalpost
    Når jeg ber om gyldige avviksvalg for mottaksregistrert journalpost
    Så skal listen med avvikstyper inneholde 'BESTILL_ORIGINAL'

  Scenario: bidrag-dokument-journalpost - Behandle BESTILL_ORIGINAL og sjekk at vi kan hente journalposten.
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200
    Og jeg henter journalpost
    Så skal http status være 200

  Scenario: bidrag-dokument-journalpost - Sjekk at avviksvalg for gitt journalpost ikke inneholder BESTILL_ORIGINAL
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Når jeg behandler avvik på opprettet journalpost
    Og jeg ber om gyldige avviksvalg for mottaksregistrert journalpost
    Så skal http status være 200
    Og så skal listen med avvikstyper ikke inneholde 'BESTILL_ORIGINAL'

  Scenario: bidrag-dokument-journalpost - Sjekk at oppgave blir laget for BESTILL_ORIGINAL
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Når jeg behandler avvik på opprettet journalpost
    Og jeg søker etter oppgaver for mottaksregistrert journalpost
    Så skal http status for oppgavesøket være 200
    Og søkeresultatet skal inneholde 1 oppgave
