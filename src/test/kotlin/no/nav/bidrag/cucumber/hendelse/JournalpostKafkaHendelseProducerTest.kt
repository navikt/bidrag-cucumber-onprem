package no.nav.bidrag.cucumber.hendelse

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.bidrag.commons.CorrelationId
import no.nav.bidrag.cucumber.model.CucumberTestsModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@DisplayName("JournalpostKafkaHendelseProducer")
internal class JournalpostKafkaHendelseProducerTest {

    @Mock
    private lateinit var objectMapperMock: ObjectMapper

    @BeforeEach
    fun `set correlation id for thread`() {
        CorrelationId.generateTimestamped("test")
    }

    @BeforeEach
    fun `skal ikke være sanity check`() {
        CucumberTestsModel(sanityCheck = false).initCucumberEnvironment()
    }
}
