package com.copago.petglam.config

import com.copago.petglam.chat.service.RedisChatMessageSubscriber
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory
        template.keySerializer = StringRedisSerializer()

        val objectMapper = ObjectMapper().registerModule(JavaTimeModule())
        template.valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        return template
    }

    @Bean
    fun chatMessageTopic(): ChannelTopic {
        return ChannelTopic("chat:messages")
    }

    @Bean
    fun redisMessageListenerContainer(connectionFactory: RedisConnectionFactory,
                                      chatMessageListenerAdapter: MessageListenerAdapter,
                                      chatMessageTopic: ChannelTopic): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(chatMessageListenerAdapter, chatMessageTopic)
        return container
    }

    @Bean
    fun chatMessageListenerAdapter(subscriber: RedisChatMessageSubscriber): MessageListenerAdapter {
        return MessageListenerAdapter(subscriber, "onMessage")
    }
}