package no.nav.bidrag.cucumber.onprem.dokument

import io.cucumber.java8.No
import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.DokumentReferanse

@Suppress("unused") // used by cucumber
class BindersEgenskaper : No {
    private lateinit var dokumentreferanse: DokumentReferanse

    init {
        Når("jeg ber om tilgang til dokument på journalpostId {string} og dokumentreferanse {string}") { journalpostId: String, dokumentreferanse: String ->
            CucumberTestRun.hentRestTjenesteTilTesting().exchangeGet("/tilgang/$journalpostId/$dokumentreferanse")
            this.dokumentreferanse = DokumentReferanse(dokumentreferanse)
        }

        Og("dokument url skal være gyldig") {
            TODO("Not yet implemented")
        }
    }
}
