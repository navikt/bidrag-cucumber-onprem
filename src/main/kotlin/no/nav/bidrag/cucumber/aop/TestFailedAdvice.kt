package no.nav.bidrag.cucumber.aop

import no.nav.bidrag.cucumber.model.CucumberTestRun
import no.nav.bidrag.cucumber.model.TestFailedException
import no.nav.bidrag.cucumber.onprem.TestDataManager
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class TestFailedAdvice {

    companion object {
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(TestFailedAdvice::class.java)
    }

    @ResponseBody
    @ExceptionHandler
    fun handleTestFailedException(testFailedException: TestFailedException) = ResponseEntity
        .status(cleanDatabase(HttpStatus.NOT_ACCEPTABLE))
        .header(HttpHeaders.WARNING, warningFrom(testFailedException))
        .body(testFailedException.suppressedStaskTraceLog)

    @ResponseBody
    @ExceptionHandler
    fun handleUnknownExceptions(runtimeException: RuntimeException) = ResponseEntity
        .status(cleanDatabase(HttpStatus.INTERNAL_SERVER_ERROR))
        .header(HttpHeaders.WARNING, warningFrom(runtimeException))
        .build<Any>()

    private fun cleanDatabase(httpStatus: HttpStatus): HttpStatus {
        try {
            if (CucumberTestRun.isTestDataPresent()) {
                LOGGER.info("clean up testdata from database!")
                TestDataManager.slettTestData()
            }
        } catch (e: Exception) {
            LOGGER.error("${e.javaClass.simpleName}: ${e.message}")
        }

        return httpStatus
    }

    private fun warningFrom(runtimeException: RuntimeException) = "${runtimeException.javaClass.simpleName}: ${runtimeException.message}"
}