package com.apiece.coupon.infrastructure.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic issuanceRequestedTopic() {
        return TopicBuilder.name(IssuanceTopics.REQUESTED).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic issuanceRequestedDltTopic() {
        return TopicBuilder.name(IssuanceTopics.REQUESTED_DLT).partitions(3).replicas(1).build();
    }
}
