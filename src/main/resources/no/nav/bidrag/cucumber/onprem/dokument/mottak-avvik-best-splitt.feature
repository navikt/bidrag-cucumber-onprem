# language: no
@bdok-mot-avvik-best-splitt
Egenskap: Avvikshendelse BESTILL_SPLITTING på journalposter som er mottaksregistrert  - bidrag-dokument (REST API: /journal/*/avvik?journalstatus=M)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt nais applikasjon 'bidrag-dokument'
    Og nøkkel for testdata 'BDOK_MOT_BEST_SPLITT'
    Og avvikstype 'BESTILL_SPLITTING'
    Og opprett journalpost når den ikke finnes:
      """
        {
          "avsenderNavn" : "Cucumber Test",
          "batchNavn"    : "En batch",
          "beskrivelse"  : "Test bestill splitting på mottaksregistrert journalpost",
          "dokumentType" : "I",
          "filnavn"      : "svada.pdf",
          "journalstatus": "M",
          "skannetDato"  : "2019-01-01"
        }
      """

  Scenario: bidrag-dokument - Skal finne avviket BESTILL_SPLITTING på mottaksregistrert journalpost
    Når jeg ber om gyldige avviksvalg for mottaksregistrert journalpost
    Så skal listen med avvikstyper inneholde 'BESTILL_SPLITTING'

  Scenario: bidrag-dokument - Behandle 'BESTILL_SPLITTING' som fører til at journalposten som nå er slettet
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Og avvikstypen har beskrivelse 'etter avsnitt 2'
    Så skal http status være 200
    Og jeg henter journalpost
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AS'
