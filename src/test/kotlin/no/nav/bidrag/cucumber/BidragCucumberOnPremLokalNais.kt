package no.nav.bidrag.cucumber

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJwtTokenValidation(ignore = ["org.springframework", "org.springdoc"])
class BidragCucumberOnPremLokalNais

fun main(args: Array<String>) {
    val app = SpringApplication(BidragCucumberOnPremLokalNais::class.java)
    app.setAdditionalProfiles("lokal-nais", "lokal-nais-secrets", "lokal", "nais", "lokal-db-nais-secrets")
    app.run(*args)
}
