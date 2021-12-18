# language: no
@bdok-mot-avvik-inn2ut
Egenskap: Avvikshendelse INNG_TIL_UTG_DOKUMENT på journalposter som er mottaksregistrert - bidrag-dokument (REST API: /journal/*/avvik?journalstatus=M)

  Bakgrunn: Gitt resttjeneste og testdata
    Gitt nais applikasjon 'bidrag-dokument'
    Og nøkkel for testdata 'BDOK_MOT_INN2UT'
    Og avvikstype 'INNG_TIL_UTG_DOKUMENT'
    Og opprett journalpost når den ikke finnes:
        """
        {
        "avsenderNavn"   : "Cucumber Test",
        "beskrivelse"    : "Test bestill inn til utgående på mottaksregistrert journalpost",
        "dokumentType"   : "I",
        "journalstatus"  : "M"
        }
        """

  Scenario: bidrag-dokument - Skal finne avviket INNG_TIL_UTG_DOKUMENT på mottaksregistrert journalpost
    Når jeg ber om gyldige avviksvalg for mottaksregistrert journalpost
    Så skal listen med avvikstyper inneholde 'INNG_TIL_UTG_DOKUMENT'

  Scenario: bidrag-dokument - Behandle INNG_TIL_UTG_DOKUMENT, og sjekk at dokumenttypen er endret til utgående.
    Gitt avviksdetaljer 'enhetsnummer' = '4806'
    Når jeg behandler avvik på opprettet journalpost
    Så skal http status være 200
    Og jeg henter journalpost
    Så skal http status være 200
    Og så skal responsen inneholde et objekt med navn 'journalpost' som har feltet 'dokumentType' = 'U'
