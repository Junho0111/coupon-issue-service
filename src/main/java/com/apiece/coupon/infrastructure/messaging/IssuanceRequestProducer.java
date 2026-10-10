package com.apiece.coupon.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class IssuanceRequestProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public IssuanceRequestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // 유저ID를 카프카 키로 만듬
    // 각 파티션에 메시즈를 고르게 분할하기 위함
    public void publish(IssuanceRequested event) {
        kafkaTemplate.send(IssuanceTopics.REQUESTED, String.valueOf(event.getUserId()), event);
    }
}
