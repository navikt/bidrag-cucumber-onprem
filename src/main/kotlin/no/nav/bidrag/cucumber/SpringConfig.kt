package no.nav.bidrag.cucumber

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import no.nav.bidrag.commons.ExceptionLogger
import no.nav.bidrag.commons.web.CorrelationIdFilter
import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.aop.ExceptionLoggerAspect
import no.nav.bidrag.cucumber.aop.TestFailedAdvice
import no.nav.bidrag.cucumber.model.SuppressStackTraceText
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.ssl.SSLContexts
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import java.security.cert.X509Certificate

@Configuration
class SpringConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI().info(
        Info()
            .title("bidrag-cucumber-onprem")
            .description("""Funksjonelle tester for nais applikasjoner som er deployet på cloud "on-premise"""")
            .version("v1")
    )

    @Bean
    fun suppressStackTraceText() = SuppressStackTraceText()

    @Bean
    fun correlationIdFilter() = CorrelationIdFilter()

    @Bean
    fun exceptionLogger() = ExceptionLogger(
        BidragCucumberOnprem::class.java.simpleName,
        ExceptionLoggerAspect::class.java,
        TestFailedAdvice::class.java
    )

    @Bean
    fun httpComponentsClientHttpRequestFactorySomIgnorererHttps(): HttpComponentsClientHttpRequestFactory {
        val acceptingTrustStrategy = { _: Array<X509Certificate>, _: String -> true }
        val sslContext = SSLContexts.custom()
            .loadTrustMaterial(null, acceptingTrustStrategy)
            .build()

        val csf = SSLConnectionSocketFactory(sslContext)

        val httpClient = HttpClients.custom()
//            .setSSLSocketFactory(csf)
            .build()

        val requestFactory = HttpComponentsClientHttpRequestFactory()

        requestFactory.httpClient = httpClient

        return requestFactory
    }
}

@Configuration
class PrototypeSpringConfig {

    @Bean
    @Scope("prototype")
    fun httpHeaderRestTemplate(httpComponentsClientHttpRequestFactory: HttpComponentsClientHttpRequestFactory): HttpHeaderRestTemplate {
        return HttpHeaderRestTemplate(httpComponentsClientHttpRequestFactory)
    }
}
