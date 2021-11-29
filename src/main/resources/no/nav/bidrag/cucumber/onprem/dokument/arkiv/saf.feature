# language: no
@arkiv-saf
Egenskap: bidrag-dokument-arkiv SAF

  Tester REST API til endepunkt i bidrag-dokument-arkiv sammen med data i dokumentarkiv

  Bakgrunn: REST grensesnitt for nais applikasjon bidrag-dokument-arkiv sammen med opprettet journalpost på et saksnummer
    Gitt nais applikasjon 'bidrag-dokument-arkiv'
    Og det finnes en ferdigstilt journalpost for saksnummer '1001001' på fagområdet 'BID'

  Scenario: @arkiv-saf: Sjekk at henting av journal resulterer i ei liste (SAF-grensesnitt, testbruker må ha rolle GOSYS_NASJONAL)
    Når jeg kaller endpoint '/sak/{saksnummer}/journal' med saksnummer på fagområde
    Så skal http status være 200
    Og så skal responsen være ei liste som ikke er tom

  Scenario: @arkiv-saf Finn opprettet journalpost via SAF query (SAF-grensesnitt, testbruker må ha rolle GOSYS_NASJONAL)
    Når jeg kaller endpoint '/sak/{saksnummer}/journal' med saksnummer på fagområde
    Så skal http status være 200
    Og så skal responsen inneholde en journalført journalpost

  Scenario: @arkiv-saf Sjekk at hentet journalpost har riktig prefix i journalpost id (SAF-grensesnitt, testbruker må ha rolle GOSYS_NASJONAL)
    Når jeg kaller endpoint '/sak/{saksnummer}/journal' med saksnummer på fagområde
    Så skal http status være 200
    Og så skal responsen inneholde en journalpost med JOARK prefix
