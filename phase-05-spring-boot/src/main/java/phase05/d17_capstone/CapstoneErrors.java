package phase05.d17_capstone;

import java.net.URI;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import phase05.d17_capstone.CapstoneFailures.Conflict;
import phase05.d17_capstone.CapstoneFailures.InvalidOrder;
import phase05.d17_capstone.CapstoneFailures.InvalidPage;
import phase05.d17_capstone.CapstoneFailures.NotFound;

/** B6: capstone MVC errors, fixed public details only. Filter 401/403 handlers remain Task7-owned. */
@RestControllerAdvice
public class CapstoneErrors extends ResponseEntityExceptionHandler {
    /** Standard MVC failures (malformed JSON, 415, type mismatch, record ctor) keep status, drop internal detail. */
    @Override protected ResponseEntity<Object> handleExceptionInternal(Exception failure, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        throw new UnsupportedOperationException("TODO B6");
    }
    @ExceptionHandler({InvalidOrder.class, InvalidPage.class}) public ProblemDetail invalid(RuntimeException failure) {
        throw new UnsupportedOperationException("TODO B6");
    }
    @ExceptionHandler(NotFound.class) public ProblemDetail missing(NotFound failure) {
        throw new UnsupportedOperationException("TODO B6");
    }
    @ExceptionHandler(Conflict.class) public ProblemDetail conflict(Conflict failure) {
        throw new UnsupportedOperationException("TODO B6");
    }
    @ExceptionHandler(DataAccessException.class) public ProblemDetail database(DataAccessException failure) {
        throw new UnsupportedOperationException("TODO B6");
    }
    private static ProblemDetail problem(HttpStatusCode status, String detail) {
        throw new UnsupportedOperationException("TODO B6");
    }
}
