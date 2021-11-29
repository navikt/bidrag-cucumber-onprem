# language: no
@bda-samt-bdj
Egenskap: bidrag-dokument hub

  Tester at REST api i bidrag-dokument snakker med bidrag-dokument-arkiv og bidrag-dokument-journalpost

  Bakgrunn: REST for bidrag-dokument
    Gitt nais applikasjon 'bidrag-dokument'

  Scenario: Henter journalposter sammensatt av journalpost fra bidrag-dokument-arkiv og bidrag-dokument-journalpost
    Gitt saksnummer '1002003' og fagområdet 'BID'
    Og at det finnes en ferdigstilt journalpost i arkiv på fagområdet og saksnummer
    Og at det finnes en journalført journalpost i midlertidig brevlager på fagområde og saksnummer
    Så skal journalposter fra arkiv og bidrag-dokument-journalpost kombineres
