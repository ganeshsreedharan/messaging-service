package com.visable.exercise.messagingservice.controller.dto

import com.visable.exercise.messagingservice.model.MessageUser
import org.hibernate.validator.constraints.Length
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank


data class MessageUserDto(
    @get:NotBlank(message = "user nick name should not be null or blank")
    @get:Length(min = 2,max = 10,message = "user nick name length should be between 2 to 10")
    val nickName: String
)

fun MessageUserDto.toUser() = MessageUser(nickName = nickName, createdDate = LocalDateTime.now())

data class MessageContentDto(
    @get:Min(value = 1, message = "Recipient id(to) should greater than zero ")
    val to: Long,
    @get:NotBlank(message = "Message content should not be null or blank ")
    val content: String
)

data class SendMessagesQuery(
    val to: Long?,
    val page: Long = 0 ,
    val size: Long = 10,
    val sort:String ="createdDate",
    val dir:String =Sort.Direction.DESC.toString()
)

data class ReceiveMessagesQuery(
    var from: Long?,
    var page: Long = 0,
    var size: Long = 10,
    val sort:String ="createdDate",
    val dir:String =Sort.Direction.DESC.toString()
)