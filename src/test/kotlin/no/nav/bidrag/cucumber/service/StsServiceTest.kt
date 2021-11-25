package no.nav.bidrag.cucumber.service

import no.nav.bidrag.commons.web.HttpHeaderRestTemplate
import no.nav.bidrag.cucumber.STS_URL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

@SpringBootTest
internal class StsServiceTest {

    @MockBean
    private lateinit var httpHeaderRestTemplateMock: HttpHeaderRestTemplate

    @Autowired
    private lateinit var stsService: StsService

    @Test
    fun `skal hente token for service bruker`() {
        System.setProperty(STS_URL, "https://sts-url")
        whenever(httpHeaderRestTemplateMock.exchange(anyString(), eq(HttpMethod.GET), anyOrNull(), eq(Map::class.java)))
            .thenReturn(ResponseEntity.ok(mapOf("idToken" to "secured")))

        val token = stsService.hentServiceBrukerOidcToken()

        assertThat(token).isEqualTo("secured")

        verify(httpHeaderRestTemplateMock).exchange("https://sts-url/rest/v1/sts/token2", HttpMethod.GET, null, Map::class.java)
    }
}
