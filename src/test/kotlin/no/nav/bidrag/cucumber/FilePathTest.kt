package no.nav.bidrag.cucumber

import no.nav.bidrag.cucumber.model.FilePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File

internal class FilePathTest {
    @Test
    fun `skal finne features i onprem katalogen`() {
        val filePath = FilePath("features.path")
        val pathFile = filePath.findFile()

        assertAll(
            { assertThat(pathFile.exists()).`as`("file exists").isTrue() },
            {
                assertThat(filePath.findFolderPath()).`as`("folder path")
                    .isEqualTo(File("src/main/resources/no/nav/bidrag/cucumber/onprem").absolutePath)
            },
        )
    }
}