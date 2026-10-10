package com.apiece.coupon.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IssuanceWorker {

    private final IssuanceWriter issuanceWriter;

    public IssuanceWorker(IssuanceWriter issuanceWriter) {
        this.issuanceWriter = issuanceWriter;
    }

    @KafkaListener(
            topics = IssuanceTopics.REQUESTED,
            groupId = IssuanceTopics.CONSUMER_GROUP,
            concurrency = "3"
    )

    public void consume(IssuanceRequested event) {
        issuanceWriter.write(event);
    }
}
