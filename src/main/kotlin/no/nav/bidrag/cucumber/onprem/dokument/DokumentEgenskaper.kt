package no.nav.bidrag.cucumber.onprem.dokument

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.onprem.dokument.arkiv.ArkivManager

@Suppress("unused") // used by cucumber
class DokumentEgenskaper : No {
    private lateinit var fagomrade: String
    private lateinit var saksnummer: String

    init {
        Gitt("saksnummer {string} og fagområdet {string}") { saksnummer: String, fagomrade: String ->
            this.saksnummer = saksnummer
            this.fagomrade = fagomrade
        }

        Og("at det finnes en ferdigstilt journalpost i arkiv på fagområdet og saksnummer") {
            ArkivManager.opprettFerdistiltJournalpostForSaksnummerNarDenIkkeFinnes(saksnummer, fagomrade)
        }

        Og("at det finnes en journalført journalpost i midlertidig brevlager på fagområde og saksnummer") {
            DokumentManager.opprettJournalfortJournalpostNarDenIkkeFinnesFraFor(saksnummer, fagomrade)
        }

        Så("skal journalposter fra arkiv og bidrag-dokument-journalpost kombineres") {
            DokumentManager.sjekkAtJournalposterSomHentesErBadeFraArkivOgBidragDokumentJournalpost(fagomrade, saksnummer)
        }
    }
}
