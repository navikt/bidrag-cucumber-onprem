# language: no
@bdj-mot-avvik-best-splitt
Egenskap: Avvikshendelse BESTILL_SPLITTING på journalposter som er mottaksregistrert - bidrag-dokument-journalpost (REST API: /journal/*/avvik?journalstatus=M)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt nais applikasjon 'bidrag-dokument-journalpost'
    Og nøkkel for testdata 'BDJ_MOT_BEST_SPLITT'
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

  Scenario: bidrag-dokument-journalpost - Skal finne avviket BESTILL_SPLITTING på mottaksregistrert journalpost
    Når jeg ber om gyldige avviksvalg for mottaksregistrert journalpost
    Så skal listen med avvikstyper inneholde 'BESTILL_SPLITTING'

  Scenario: bidrag-dokument-journalpost - Behandle 'BESTILL_SPLITTING' som fører til at journalposten som nå er slettet
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Og avvikstypen har beskrivelse 'etter avsnitt 2'
    Så skal http status være 200
    Og jeg henter journalpost
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'slettet' = 'true'
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AS'
