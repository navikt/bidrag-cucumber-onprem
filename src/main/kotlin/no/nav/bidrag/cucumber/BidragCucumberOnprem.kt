package no.nav.bidrag.cucumber

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class BidragCucumberOnprem {
    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(BidragCucumberOnprem::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val profile = if (args.isEmpty()) {
                PROFILE_LIVE
            } else {
                LOGGER.info("Starter med profil (argument): $args")
                args[0]
            }

            val app = SpringApplication(BidragCucumberOnprem::class.java)

            app.setAdditionalProfiles(profile)
            app.run(*args)
        }
    }
}
