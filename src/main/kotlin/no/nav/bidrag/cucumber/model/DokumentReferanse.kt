package no.nav.bidrag.cucumber.model

data class DokumentReferanse ( var dokumentReferanse: String ) {
    fun hentMedErstattetSpesialtegn() = dokumentReferanse
            .replace(":", "%3A")
            .replace("/", "%2F")
            .replace("?", "%3F")
            .replace("=", "%3D")
            .replace("&", "%26")
            .replace("#", "%23")
}
