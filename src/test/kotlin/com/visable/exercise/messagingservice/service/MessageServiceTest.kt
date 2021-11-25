package com.visable.exercise.messagingservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.visable.exercise.messagingservice.config.LoggedInUserData
import com.visable.exercise.messagingservice.controller.dto.MessageContentDto
import com.visable.exercise.messagingservice.controller.dto.ReceiveMessagesQuery
import com.visable.exercise.messagingservice.controller.dto.SendMessagesQuery
import com.visable.exercise.messagingservice.exception.UserNotFoundException
import com.visable.exercise.messagingservice.exception.UserNotPermittedException
import com.visable.exercise.messagingservice.model.Message
import com.visable.exercise.messagingservice.model.MessageUser
import com.visable.exercise.messagingservice.model.toMessagePayload
import com.visable.exercise.messagingservice.repository.MessageRepository
import com.visable.exercise.messagingservice.service.queue.SendMessageHandlerService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.*
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties=
["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"])
class MessageServiceTest @Autowired constructor(val messageService: MessageService) {

    @MockBean
    lateinit var messageRepository: MessageRepository

    @MockBean
    lateinit var loggedInUserData: LoggedInUserData

    @MockBean
    lateinit var sendMessageHandlerService: SendMessageHandlerService

    @MockBean
    lateinit var userService: UserService

    lateinit var loggedInUser: MessageUser

    lateinit var recieverUser: MessageUser

    lateinit var sendMessagesQuery:SendMessagesQuery

    lateinit var recieveMessageQuery:ReceiveMessagesQuery

    @BeforeEach
    fun setUp() {
        loggedInUser = MessageUser(1, "Test User 1", LocalDateTime.now())
        recieverUser = MessageUser(2, "Test User 2", LocalDateTime.now())
        JacksonTester.initFields(this, ObjectMapper())
        val message = Message(
            messageId = 1, loggedInUser, recieverUser, "test msg", LocalDateTime.now()
        )
        Mockito.`when`(messageRepository.save(Mockito.any())).thenReturn(
            message
        )
        val result: Page<Message> = PageImpl(listOf(message))
        recieveMessageQuery=  ReceiveMessagesQuery(page =0,size = 5,sort = "createdDate",dir = "DESC",from = null)
        val pageable1: Pageable = PageRequest.of(recieveMessageQuery.page.toInt(), recieveMessageQuery.size.toInt(), Sort.by(Sort.Direction.valueOf(recieveMessageQuery.dir), recieveMessageQuery.sort))
        Mockito.`when`(messageRepository.findAllByReceiverUserId(2,pageable1)).thenReturn(result)

        sendMessagesQuery=  SendMessagesQuery(page =0,size = 5,sort = "createdDate",dir = "DESC",to = null)
        val pageable2: Pageable = PageRequest.of(sendMessagesQuery.page.toInt(), sendMessagesQuery.size.toInt(), Sort.by(Sort.Direction.valueOf(recieveMessageQuery.dir), recieveMessageQuery.sort))
        Mockito.`when`(messageRepository.findAllBySenderUserId(1,pageable2)).thenReturn(result)
        doNothing().`when`(sendMessageHandlerService).sendMessageToOutgoingExchange(message.toMessagePayload())

    }

    @Test
    fun `when calling sendMessage `() {
        loggedInUserData.userDetails = loggedInUser
        Mockito.`when`(userService.getUser(1L)).thenReturn(loggedInUser)
        Mockito.`when`(userService.getUser(2L)).thenReturn(recieverUser)
        val msg = messageService.sendMessage(MessageContentDto(2, "test msg"))
        assert(msg?.messageId == 1L)
        assert(msg?.sender == loggedInUser)
        assert(msg?.receiver == recieverUser)
    }
    @Test
    fun `when recipient(to) user not found on sending message`(){
        loggedInUserData.userDetails = loggedInUser
        Mockito.`when`(userService.getUser(2)).thenReturn(null)
       val ex = assertThrows<UserNotFoundException> {  messageService.sendMessage(MessageContentDto(2, "test msg")) }
        assert(ex.message!!.contains("User not found"))

    }

    @Test
    fun `when logged in user data not initialised with userFilter`(){
        Mockito.`when`(userService.getUser(2L)).thenReturn(recieverUser)
        val ex = assertThrows<UserNotPermittedException> {  messageService.sendMessage(MessageContentDto(2, "test msg")) }
        assert(ex.message!!.contains("cannot send message"))

    }

    @Test
    fun `when getting received messages`(){
        Mockito.`when`(userService.getUser(2L)).thenReturn(recieverUser)
        loggedInUserData.userDetails = recieverUser
        val result = messageService.getMessagesReceivedByUser(recieveMessageQuery)
        assert(result.content.size >0)
        assert(result.content[0].receiver.userId == recieverUser.userId)
    }

    @Test
    fun `when getting received messages - when logged in user data not initialised with userFilter`(){
        Mockito.`when`(userService.getUser(2L)).thenReturn(recieverUser)
        assertThrows<UserNotPermittedException> {
             messageService.getMessagesReceivedByUser(recieveMessageQuery)
        }
    }

}