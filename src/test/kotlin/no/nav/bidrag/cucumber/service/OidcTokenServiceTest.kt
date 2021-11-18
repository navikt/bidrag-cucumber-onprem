package no.nav.bidrag.cucumber.service

import no.nav.bidrag.cucumber.Fasit
import no.nav.bidrag.cucumber.NAV_AUTH
import no.nav.bidrag.cucumber.TEST_AUTH
import no.nav.bidrag.cucumber.Url
import no.nav.bidrag.cucumber.dto.CucumberTestsApi
import no.nav.bidrag.cucumber.model.CucumberTestRun
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.net.URI

@SpringBootTest
internal class OidcTokenServiceTest {

    @MockBean
    private lateinit var applicationContextMock: ApplicationContext

    @MockBean
    private lateinit var restTemplateMock: RestTemplate

    @Autowired
    private lateinit var oidcTokenService: OidcTokenService

    @BeforeEach
    fun `applicationContext henter RestTemplate som mock`() {
        whenever(applicationContextMock.getBean(RestTemplate::class.java)).thenReturn(restTemplateMock)
    }

    @Test
    fun `skal generere oidc token`() {
        CucumberTestRun(CucumberTestsApi(navUsername = "j104364", testUsername = "z992903"))
            .initEnvironment()

        System.setProperty("${NAV_AUTH}_J104364", "mySecret")
        System.setProperty("${TEST_AUTH}_Z992903", "testSecret")

        whenever(restTemplateMock.getForObject(anyString(), eq(String::class.java))).thenReturn(
            """[{
                "type": "openidconnect",
                "alias": "${Fasit.ALIAS_OIDC}",
                "properties": {}
            }]
            """.trimIndent().trimEnd()
        )

        whenever(restTemplateMock.exchange(eq("/"), eq(HttpMethod.GET), any(), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("{}"))

        whenever(restTemplateMock.exchange(eq(Url.ISSO), eq(HttpMethod.POST), any(), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("""{"tokenId":"xyz123"}"""))

        whenever(restTemplateMock.postForLocation(eq(Url.ISSO_AUTHORIZE), any())).thenReturn(URI("https://some?query=svada&code=somewhere"))
        whenever(restTemplateMock.exchange(eq(Url.ISSO_ACCESS_TOKEN), eq(HttpMethod.POST), any(), eq(String::class.java)))
            .thenReturn(ResponseEntity.ok("""{"id_token":"xyz123"}"""))

        val token = oidcTokenService.generateToken("my-app")

        assertThat(token).isEqualTo("xyz123")
    }
}
