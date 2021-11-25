package com.visable.exercise.messagingservice.service.queue

import com.visable.exercise.messagingservice.config.RabbitMQConfig
import com.visable.exercise.messagingservice.controller.MessagingServiceControllerAdvice
import com.visable.exercise.messagingservice.model.MessagePayload
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry
import org.springframework.stereotype.Service


@Service
class ReceiveMessageListener(
    private val amqpAdmin: AmqpAdmin,
    private val rabbitListenerEndpointRegistry: RabbitListenerEndpointRegistry
) {

    @RabbitListener(queues = [RabbitMQConfig.CHAT_MESSAGING_QUEUE])
    fun listenToChatMessagingQueue(payload: MessagePayload) {
        log.info("message received $payload")
        //All kind of side effect actions can do here like push notification, message content validations, reporting etc...
    }

    companion object {
        private val log = LoggerFactory.getLogger(MessagingServiceControllerAdvice::class.java)
        private const val LISTENER_CONTAINER = "queue_listener_container"
    }
}