package com.visable.exercise.messagingservice.config

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableRabbit
class RabbitMQConfig {

    companion object {
        const val CHAT_MESSAGING_QUEUE = "chat_messaging_queue"
        const val OUTGOING_EXCHANGE = "outgoing_exchange"
    }

    @Bean
    fun createChatMessagingQueue(): Queue = QueueBuilder.durable(CHAT_MESSAGING_QUEUE).build()


    //preferring fanout exchange same message can push to multiple queues for processing different ways
    @Bean
    fun createOutgoingExchange(): FanoutExchange = ExchangeBuilder.fanoutExchange(OUTGOING_EXCHANGE).build()


    @Bean
    fun createChatMessagingBinding(): Binding = BindingBuilder.bind(createChatMessagingQueue()).to(createOutgoingExchange())

}