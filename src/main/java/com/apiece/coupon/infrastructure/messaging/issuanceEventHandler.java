package com.apiece.coupon.infrastructure.messaging;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class issuanceEventHandler {

    private final IssuanceWriter issuanceWriter;

    public issuanceEventHandler(IssuanceWriter issuanceWriter) {
        this.issuanceWriter = issuanceWriter;
    }

    @Async(AsyncIssuanceConfig.ISSUANCE_TASK_EXECUTOR)
    @EventListener
    public void handle(IssuanceRequested event) {
        issuanceWriter.write(event);
    }
}
