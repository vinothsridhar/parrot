package ai.sridhar.faq

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidRequestException(message: String, val clientMessage: String? = null) : RuntimeException(message)

@ControllerAdvice
class ExceptionControllerAdvice: ResponseEntityExceptionHandler() {

    @ExceptionHandler
    fun handleInvalidRequest(exception: InvalidRequestException): ResponseEntity<ErrorDetail> {
        logger.error("handleInvalidRequest Exception: ", exception)
        return ResponseEntity.badRequest().body(
            ErrorDetail(
                "Invalid Request",
                exception.message ?: "Exception"
            )
        )
    }
}

data class ErrorDetail(
    var reason: String,
    var message: String
)