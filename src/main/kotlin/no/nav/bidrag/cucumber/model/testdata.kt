package no.nav.bidrag.cucumber.model

internal class TestData {
    var nokkel: String? = null
    val dataForNokkel: MutableMap<String, Data> = HashMap()

    fun isDataPresent() = dataForNokkel.isNotEmpty()
    fun harIkkeLagretTestdata(nokkel: String) = !dataForNokkel.contains(nokkel) || dataForNokkel[nokkel]?.journalpostId == null
    fun hentJournalpostId(nokkel: String) = dataForNokkel[nokkel]?.journalpostId ?: throw IllegalStateException("Ingen testdata på $nokkel!")
    fun hentSaksnummer(nokkel: String) = dataForNokkel[nokkel]?.saksnummer ?: throw IllegalStateException("Ingen testdata på $nokkel!")
    fun nye(nokkel: String, journalpostId: String, saksnummer: String) {
        dataForNokkel[nokkel] = Data(journalpostId = journalpostId, saksnummer = saksnummer, opprinneligeData = dataForNokkel[nokkel])
    }
}

internal data class Data(
    val avvik: Avvik = Avvik(),
    val journalpostId: String? = null,
    val saksnummer: String? = null
) {
    constructor(journalpostId: String, saksnummer: String, opprinneligeData: Data?) : this(journalpostId = journalpostId, saksnummer = saksnummer) {
        avvik.avvikstype = opprinneligeData?.avvik?.avvikstype
        avvik.beskrivelse = opprinneligeData?.avvik?.beskrivelse

        opprinneligeData?.let { avvik.avviksdetaljer.putAll(it.avvik.avviksdetaljer) }
    }
}

const val ENHETSNUMMER_FOR_AVVIK = "enhetsnummer"

internal data class Avvik(
    var avvikstype: String? = null,
    val avviksdetaljer: MutableMap<String, String> = HashMap(),
    var beskrivelse: String? = null
) {

    fun mapAvviksdetaljer() = if (avviksdetaljer.isEmpty()) null else BidragCucumberSingletons.mapTilJson(avviksdetaljer)
    fun hentBeskrivelse() = beskrivelse
    fun leggTilDetalj(detalj: String, detaljVerdi: String) {
        avviksdetaljer[detalj] = detaljVerdi
    }
}
