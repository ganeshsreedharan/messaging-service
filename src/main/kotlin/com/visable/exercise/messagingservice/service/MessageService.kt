package com.visable.exercise.messagingservice.service


import com.visable.exercise.messagingservice.config.LoggedInUserData
import com.visable.exercise.messagingservice.controller.dto.MessageContentDto
import com.visable.exercise.messagingservice.controller.dto.ReceiveMessagesQuery
import com.visable.exercise.messagingservice.controller.dto.SendMessagesQuery
import com.visable.exercise.messagingservice.exception.UserNotFoundException
import com.visable.exercise.messagingservice.exception.UserNotPermittedException
import com.visable.exercise.messagingservice.model.Message
import com.visable.exercise.messagingservice.model.toMessagePayload
import com.visable.exercise.messagingservice.repository.MessageRepository
import com.visable.exercise.messagingservice.service.queue.SendMessageHandlerService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val loggedInUserData: LoggedInUserData,
    private val userService: UserService,
    private val sendMessageHandlerService: SendMessageHandlerService
) {

    fun sendMessage(message: MessageContentDto): Message? {
         userService.getUser(message.to)?.let {
            if (Objects.nonNull(loggedInUserData.userDetails) && it.userId != loggedInUserData.userDetails?.userId) {
                val messageToDb = Message(
                    sender = loggedInUserData.userDetails!!,
                    receiver = it,
                    createdDate = LocalDateTime.now(),
                    messageContent = message.content
                )
                //publishing messages to queue
                sendMessageHandlerService.sendMessageToOutgoingExchange(messageToDb.toMessagePayload())
                return messageRepository.save(messageToDb)
            } else {
                throw UserNotPermittedException("You cannot send message to your self")
            }
        }
        throw UserNotFoundException("User not found exception ${message.to}")
    }

    fun getMessagesSendByUser(messageQueryDto: SendMessagesQuery): Page<Message> {
        val userId = loggedInUserData.userDetails?.userId
        userId?.let {
            messageQueryDto.to?.let {
                userService.getUser(it)
                return messageRepository.findAllBySenderUserIdAndReceiverUserId(
                    userId,
                    it,
                    PageRequest.of(
                        messageQueryDto.page.toInt(),
                        messageQueryDto.size.toInt(),
                        Sort.by(Sort.Direction.valueOf(messageQueryDto.dir), messageQueryDto.sort)
                    )
                )
            }
            return messageRepository.findAllBySenderUserId(
                userId,
                PageRequest.of(
                    messageQueryDto.page.toInt(),
                    messageQueryDto.size.toInt(),
                    Sort.by(Sort.Direction.valueOf(messageQueryDto.dir), messageQueryDto.sort)
                )
            )
        }
        throw UserNotPermittedException("Invalid user ID")
    }


    fun getMessagesReceivedByUser(messageQueryDto: ReceiveMessagesQuery): Page<Message> {
        val userId = loggedInUserData.userDetails?.userId
        userId?.let {
            messageQueryDto.from?.let {
                return messageRepository.findAllBySenderUserIdAndReceiverUserId(
                    it,
                    userId,
                    PageRequest.of(
                        messageQueryDto.page.toInt(),
                        messageQueryDto.size.toInt(),
                        Sort.by(Sort.Direction.valueOf(messageQueryDto.dir), messageQueryDto.sort)
                    )
                )
            }
            return messageRepository.findAllByReceiverUserId(
                userId,
                PageRequest.of(
                    messageQueryDto.page.toInt(),
                    messageQueryDto.size.toInt(),
                    Sort.by(Sort.Direction.valueOf(messageQueryDto.dir), messageQueryDto.sort)
                )
            )
        }
        throw UserNotPermittedException("Invalid user ID")
    }
}