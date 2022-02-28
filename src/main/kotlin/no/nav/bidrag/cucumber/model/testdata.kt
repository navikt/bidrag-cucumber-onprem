package no.nav.bidrag.cucumber.model

import no.nav.bidrag.cucumber.onprem.dokument.DokumentEgenskaper.Companion.ARKIVER_JOURNALPOST_NOKKEL
import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger(TestData::class.java)

internal class TestData {
    var avvikstype: String? = null
    var nokkel: String? = null
    val dataForNokkel: MutableMap<String, Data> = HashMap()

    fun isDataPresent() = dataForNokkel.filter { it.key != ARKIVER_JOURNALPOST_NOKKEL }.isNotEmpty()
    fun harIkkeLagretTestdata(nokkel: String) = !dataForNokkel.contains(nokkel) || dataForNokkel[nokkel]?.journalpostId == null
    fun hentJournalpostId(nokkel: String?) = dataForNokkel[nokkel]?.journalpostId ?: throwIllegalStateException("Ingen testdata på $nokkel!")
    fun hentJoarkJournalpostId(nokkel: String?) = dataForNokkel[nokkel]?.joarkJournalpostId ?: throwIllegalStateException("Ingen testdata på $nokkel!")
    fun hentJournalpostIdUtenPrefix(nokkel: String?) = hentJournalpostId(nokkel).split("-")[1]
    fun hentSaksnummer(nokkel: String) = dataForNokkel[nokkel]?.saksnummer ?: throwIllegalStateException("Ingen testdata på $nokkel!")
    fun nye(nokkel: String, data: Data) {
        dataForNokkel[nokkel] = data.berikFra(dataForNokkel[nokkel])
    }

    private fun throwIllegalStateException(message: String): String {
        if (CucumberTestRun.isSanityCheck) {
            return "BID--1"
        }

        throw IllegalStateException(message)
    }

}

internal data class Data(
    val avvik: Avvik = Avvik(),
    var fagomrade: String? = null,
    var journalpostId: String? = null,
    var joarkJournalpostId: String? = null,
    var saksnummer: String? = null
) {

    fun berikFra(data: Data?): Data {
        if (data != null) {
            avvik.berikMed(data.avvik)
            fagomrade = nyVerdi("fagomrade", fagomrade, data.fagomrade)
            journalpostId = nyVerdi("journalpostId", journalpostId, data.journalpostId)
            joarkJournalpostId = nyVerdi("joarkJournalpostId", joarkJournalpostId, data.joarkJournalpostId)
            saksnummer = nyVerdi("saksnummer", saksnummer, data.saksnummer)
        }

        return this
    }
}

internal data class Avvik(
    var avvikstype: String? = null,
    val avviksdetaljer: MutableMap<String, String> = HashMap(),
    var beskrivelse: String? = null
) {

    fun mapAvviksdetaljer() = if (avviksdetaljer.isEmpty()) null else BidragCucumberSingletons.mapTilJson(avviksdetaljer)
    fun hentBeskrivelse() = beskrivelse
    fun berikMed(avvik: Avvik) {
        avvikstype = nyVerdi("avvikstyoe", avvikstype, avvik.avvikstype)
        beskrivelse = nyVerdi("beskrivelse", beskrivelse, avvik.beskrivelse)

        avvik.avviksdetaljer.forEach {
            avviksdetaljer[it.key] = nyVerdi("avviksdetalj.${it.key}", avviksdetaljer[it.key], it.value)!!
        }
    }
}

private fun <T> nyVerdi(felt: String, verdi: T, erstattesMed: T): T {
    if (verdi == null && erstattesMed != null) {
        return erstattesMed
    }

    if (erstattesMed == null) {
        return verdi
    }

    if (verdi != erstattesMed) {
        LOGGER.warn("Testdata! Erstatter verdi i '$felt': '$verdi' med '$erstattesMed'!")
        return erstattesMed
    }

    return verdi
}