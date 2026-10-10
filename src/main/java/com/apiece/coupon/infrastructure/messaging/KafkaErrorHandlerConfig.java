package com.apiece.coupon.infrastructure.messaging;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                template,
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1_000L, 3L));
    }
}

// 특정 조건이 되어서 DLT로 메시지를 보낼때 그것이 실패하면 1초 간격으로 최대 3번 시도하겠다는 의미

/*
 *
 *  IssuanceWorker가 메시지를 처음에 받아옴
 * 그런데
 * public void consume(IssuanceRequested event) {
 *    issuanceWriter.write(event);
 * }
 * 안의 write가 무엇인가의 문제때문에 실패를 함
 * 그럼 위의 정책에 의해 1초간격을 두고 쉬고 3번 시도를 함
 * 3번 시도에도 실패를 하면 그제서야 DLT 토픽으로 해당 메시지가 전달됨.
 */