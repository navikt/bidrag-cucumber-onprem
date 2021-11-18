package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.Environment
import no.nav.bidrag.cucumber.model.CucumberTestRun

abstract class TokenService {
    abstract fun generateToken(application: String): String

    fun fetchBearerToken(application: String): String {
        val token = if (Environment.withSecurityToken) {
            Environment.securityToken
        } else if (CucumberTestRun.withSecurityToken) {
            CucumberTestRun.securityToken
        } else {
            generateToken(application)
        }

        CucumberTestRun.updateSecurityToken(token)

        return "Bearer $token"
    }
}