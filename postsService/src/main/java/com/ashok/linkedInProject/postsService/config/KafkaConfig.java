package com.ashok.linkedInProject.postsService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String POST_CREATED_TOPIC = "post-created";
    public static final String POST_LIKED_TOPIC = "post-liked";

    @Bean
    public NewTopic postCreatedTopic() {
        return TopicBuilder.name(POST_CREATED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic postLikedTopic() {
        return TopicBuilder.name(POST_LIKED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
