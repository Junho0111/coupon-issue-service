package com.apiece.coupon.support;

import org.springframework.http.HttpStatus;

/**
 * 도메인 규칙 위반. 생성자가 package-private 이라 같은 패키지 안에서만 상속할 수 있다.
 */
public abstract class DomainException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    DomainException(String code, HttpStatus httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
