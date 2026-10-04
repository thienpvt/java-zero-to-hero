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
        // SOLUTION-BEGIN throw B6
        return super.handleExceptionInternal(failure, problem(status, status.is4xxClientError()
                ? "Request is invalid." : "Request could not be completed."), headers, status, request);
        // SOLUTION-END
    }
    @ExceptionHandler({InvalidOrder.class, InvalidPage.class}) public ProblemDetail invalid(RuntimeException failure) {
        // SOLUTION-BEGIN throw B6
        return problem(HttpStatus.BAD_REQUEST, "Request is invalid.");
        // SOLUTION-END
    }
    @ExceptionHandler(NotFound.class) public ProblemDetail missing(NotFound failure) {
        // SOLUTION-BEGIN throw B6
        return problem(HttpStatus.NOT_FOUND, "Resource not found.");
        // SOLUTION-END
    }
    @ExceptionHandler(Conflict.class) public ProblemDetail conflict(Conflict failure) {
        // SOLUTION-BEGIN throw B6
        return problem(HttpStatus.CONFLICT, "Resource state conflicts with this request.");
        // SOLUTION-END
    }
    @ExceptionHandler(DataAccessException.class) public ProblemDetail database(DataAccessException failure) {
        // SOLUTION-BEGIN throw B6
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Request could not be completed.");
        // SOLUTION-END
    }
    private static ProblemDetail problem(HttpStatusCode status, String detail) {
        // SOLUTION-BEGIN throw B6
        var body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setType(URI.create("urn:phase05:capstone:" + status.value()));
        return body;
        // SOLUTION-END
    }
}
