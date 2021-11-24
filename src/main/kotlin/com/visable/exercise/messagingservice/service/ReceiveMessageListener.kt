package com.visable.exercise.messagingservice.service

import com.visable.exercise.messagingservice.config.RabbitMQConfig
import com.visable.exercise.messagingservice.controller.MessagingServiceControllerAdvice
import com.visable.exercise.messagingservice.model.MessagePayload
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.stereotype.Service


@Service
class ReceiveMessageListener(
    private val amqpAdmin: AmqpAdmin,
    private val rabbitListenerEndpointRegistry: RabbitListenerEndpointRegistry
) {


    @RabbitListener(queues = [RabbitMQConfig.CHAT_MESSAGING_QUEUE])
    fun listenToChatMessagingQueue(payload: MessagePayload) {
        println("$payload")
        //All kind of side effect actions can do here like push notification, message content validations, reporting etc...

        // Sending to another user queue for instant subscription
        //sending to the "sender user queue", the "receiver user" will be subscribing to this dynamically created sender queu
        createMessageSenderQueue(payload.sender, payload.receiver)
    }

    fun createMessageSenderQueue(sender: String, receiver: String) {
        val queue = Queue(sender + "_queue", true, false, false)
        amqpAdmin.declareQueue(queue)
        val container = rabbitListenerEndpointRegistry
            .getListenerContainer(LISTENER_CONTAINER) as SimpleMessageListenerContainer
        container.addQueueNames(queue.name)
        val binding = Binding(
            queue.name, Binding.DestinationType.QUEUE, receiver + "_exchange",
            "$receiver.$sender", null
        )
        try {
            amqpAdmin.declareBinding(binding)
        } catch (ex: Exception) {
            log.error("Exception to declare the binding ")
        }
    }



    //When user is interested on the live changes ,like need to subscribe the latest messages from Web socket,
    // need to call this method with sender name on subscribe endpoint
    fun createSenderExchange(user: String) {
        val exchange = DirectExchange(user + "_exchange")
        amqpAdmin.declareExchange(exchange)
        val binding = Binding(
            exchange.name, Binding.DestinationType.EXCHANGE, RabbitMQConfig.INCOMING_EXCHANGE,
            "$user.#", null
        )
        amqpAdmin.declareBinding(binding)
    }

    @RabbitListener(id = LISTENER_CONTAINER)
    fun senderQueueListener(listener: String, payload: MessagePayload) {
        //To get the direct events, the subscriber will be calling the createSenderExchange method on subscribing the live changes.
        //So events will be coming to this place and can be send to the user web socket.

    }

    companion object {
        private val log = LoggerFactory.getLogger(MessagingServiceControllerAdvice::class.java)
        private const val LISTENER_CONTAINER = "queue_listener_container"
    }
}