package no.nav.bidrag.cucumber.model

class TestFailedException(message: String, internal val suppressedStaskTraceLog: String) : RuntimeException(message)