package com.visable.exercise.messagingservice.service.queue

import com.visable.exercise.messagingservice.config.RabbitMQConfig
import com.visable.exercise.messagingservice.model.MessagePayload
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate
import org.springframework.stereotype.Service


@Service
class SendMessageHandlerService(
    private val rabbitMessagingTemplate: RabbitMessagingTemplate
) {

    fun sendMessageToOutgoingExchange(payload: MessagePayload) {
        rabbitMessagingTemplate.convertAndSend(RabbitMQConfig.OUTGOING_EXCHANGE, payload.sender, payload)
    }
}