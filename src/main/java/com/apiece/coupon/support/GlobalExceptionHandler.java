package com.apiece.coupon.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomain(
            DomainException ex,
            HttpServletRequest request
    ) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                ex.getHttpStatus(),
                Objects.requireNonNullElse(ex.getMessage(), "")
        );
        pd.setTitle(humanize(ex.getCode()));
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("code", ex.getCode());
        return ResponseEntity.status(ex.getHttpStatus()).body(pd);
    }

    private String humanize(String code) {
        return Arrays.stream(code.split("_"))
                .map(word -> word.isEmpty()
                        ? word
                        : word.substring(0, 1).toUpperCase(Locale.ROOT)
                                + word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }
}
