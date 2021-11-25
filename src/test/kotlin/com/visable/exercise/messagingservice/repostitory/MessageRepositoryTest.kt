package com.visable.exercise.messagingservice.repostitory

import com.visable.exercise.messagingservice.common.DataBaseInitializer
import com.visable.exercise.messagingservice.model.Message
import com.visable.exercise.messagingservice.model.MessageUser
import com.visable.exercise.messagingservice.repository.MessageRepository
import com.visable.exercise.messagingservice.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageRepositoryTest @Autowired constructor(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) : DataBaseInitializer() {


    lateinit var userA: MessageUser
    lateinit var userB: MessageUser


    @Test
    fun `When sending message from user A to user B, creating message entry`() {

        userA = userRepository.save(MessageUser(nickName = "userA", createdDate = LocalDateTime.now()))
        userB = userRepository.save(MessageUser(nickName = "userB", createdDate = LocalDateTime.now()))

        val messageToDB1 = messageRepository.save(
            Message(
                sender = userA,
                receiver = userB,
                messageContent = "test message1",
                createdDate = LocalDateTime.now()
            )
        )
        val messageToDB2 = messageRepository.save(
            Message(
                sender = userA,
                receiver = userB,
                messageContent = "test message2",
                createdDate = LocalDateTime.now()
            )
        )
        assert(Objects.nonNull(messageToDB1))
        assert(messageToDB1.messageId!! > 0)
        val bySender = messageRepository.findAllBySenderUserId(userA.userId!!, PageRequest.of(0, 5,  Sort.by(Sort.Direction.valueOf("DESC"),"createdDate" )))
        assert(bySender.totalElements > 0)
        assert(bySender.content[0].sender.userId == userA.userId)
        assert(bySender.content[0].messageId == messageToDB2.messageId)// checking message is sorted by createdDate use should get the last send message in first list

        val byReciever = messageRepository.findAllByReceiverUserId(userB.userId!!, PageRequest.of(0, 5,  Sort.by(Sort.Direction.valueOf("DESC"),"createdDate" )))

        assert(byReciever.content[0].receiver.userId == userB.userId)
    }

    @Test
    fun `When sending message from user B to user A`() {

        userA = userRepository.save(MessageUser(nickName = "userA", createdDate = LocalDateTime.now()))
        userB = userRepository.save(MessageUser(nickName = "userB", createdDate = LocalDateTime.now()))

        val messageToDB1 = messageRepository.save(
            Message(
                sender = userA,
                receiver = userB,
                messageContent = "test message1",
                createdDate = LocalDateTime.now()
            )
        )
        val messageToDB2 = messageRepository.save(
            Message(
                sender = userA,
                receiver = userB,
                messageContent = "test message2",
                createdDate = LocalDateTime.now()
            )
        )
        val messageToDB3 = messageRepository.save(
            Message(
                sender = userB,
                receiver = userA,
                messageContent = "test message2",
                createdDate = LocalDateTime.now()
            )
        )
        assert(Objects.nonNull(messageToDB1))
        assert(messageToDB1.messageId!! > 0)
        val bySender = messageRepository.findAllBySenderUserId(userA.userId!!, PageRequest.of(0, 5,  Sort.by(Sort.Direction.valueOf("DESC"),"createdDate" )))
        assert(bySender.totalElements == 2L)
        val byReciever = messageRepository.findAllByReceiverUserId(userA.userId!!, PageRequest.of(0, 5,  Sort.by(Sort.Direction.valueOf("DESC"),"createdDate" )))
        assert(byReciever.totalElements == 1L)

        val bySenderAndReciever1 = messageRepository.findAllBySenderUserIdAndReceiverUserId(userA.userId!!,userB.userId!!,PageRequest.of(0, 5,  Sort.by(Sort.Direction.valueOf("DESC"),"createdDate" )))
        assert(bySenderAndReciever1.totalElements == 2L)

        val bySenderAndReciever2 = messageRepository.findAllBySenderUserIdAndReceiverUserId(userB.userId!!,userA.userId!!,PageRequest.of(0, 5,  Sort.by(Sort.Direction.valueOf("DESC"),"createdDate" )))
        assert(bySenderAndReciever2.totalElements == 1L)

    }


}