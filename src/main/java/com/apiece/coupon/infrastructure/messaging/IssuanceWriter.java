package com.apiece.coupon.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class IssuanceWriter {

    private static final Logger log = LoggerFactory.getLogger(IssuanceWriter.class);

    private final IssuanceTransactionalWriter issuanceTransactionalWriter;

    public IssuanceWriter(IssuanceTransactionalWriter issuanceTransactionalWriter) {
        this.issuanceTransactionalWriter = issuanceTransactionalWriter;
    }

    public void write(IssuanceRequested event) {
        try {
            issuanceTransactionalWriter.insertAndIncrement(event);
        } catch (DataIntegrityViolationException e) { // 실패처리 대신 디버그 정도로만 남김, 잡지않으면 워커가 계속 재처리를 시도해버리고 해당의 경우 중복 발급관련이니
            log.debug(
                    "UNIQUE 위반은 멱등 처리: couponId={}, userId={}",
                    event.getCouponId(),
                    event.getUserId()
            );
        }
    }
}
