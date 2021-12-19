# language: no
@bdok-mot-avvik-best-reskan
Egenskap: Avvikshendelse BESTILL_RESKANNING på journalposter som er mottaksregistrert - bidrag-dokument (REST API: /journal/*/avvik?journalstatus=M)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt nais applikasjon 'bidrag-dokument'
    Og nøkkel for testdata 'BDOK_MOT_BEST_RESKAN'
    Og avvikstype 'BESTILL_RESKANNING'
    Og opprett journalpost når den ikke finnes:
      """
        {
          "avsenderNavn" : "Cucumber Test",
          "batchNavn"    : "En batch",
          "beskrivelse"  : "Test bestill reskanning på mottaksregistrert journalpost",
          "dokumentType" : "I",
          "journalstatus": "M",
          "skannetDato"  : "2019-01-01"
        }
      """

  Scenario: bidrag-dokument - Skal finne avviket BESTILL_RESKANNING på mottaksregistrert journalpost
    Når jeg ber om gyldige avviksvalg for mottaksregistrert journalpost
    Så skal listen med avvikstyper inneholde 'BESTILL_RESKANNING'

  Scenario: bidrag-dokument - Behandle 'BESTILL_RESKANNING' som fører til at journalposten er slettet
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200
    Og jeg henter journalpost
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'journalstatus' = 'AR'
