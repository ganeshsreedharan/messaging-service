package com.visable.exercise.messagingservice.controller

import com.visable.exercise.messagingservice.constant.MessagingConstants
import com.visable.exercise.messagingservice.controller.dto.*
import com.visable.exercise.messagingservice.exception.MessageNotFoundException
import com.visable.exercise.messagingservice.model.Message
import com.visable.exercise.messagingservice.service.MessageService
import io.swagger.annotations.*
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/message")
@ApiImplicitParams(
    *[
        ApiImplicitParam(
            name = MessagingConstants.USER_HEADER,
            value = "User token for identify the user",
            required = true,
            paramType = "header"
        )
    ]
)
class MessageController(
    private val messageService: MessageService,
    private val pagedResourcesAssembler: PagedResourcesAssembler<Message>
) {

    @ApiOperation(value = "Api for sending message to other user")
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "Successfully sent message to user"),
            ApiResponse(code = 404, message = "Recipient user not found"),
            ApiResponse(code = 403, message = "User not permitted to send the data"),
            ApiResponse(code = 400, message = "invalid data in request")]
    )
    @PostMapping
    fun postMessage(@Valid @RequestBody messageContentDto: MessageContentDto) =
        messageService.sendMessage(messageContentDto)

    @ApiOperation(value = "Api for getting messages send by the user")
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "Successfully sent message to user"),
            ApiResponse(code = 404, message = "Recipient user not found"),
            ApiResponse(code = 403, message = "User not permitted to send the data"),
            ApiResponse(code = 400, message = "invalid data in request")]
    )
    @GetMapping("/send")
    fun getAllSendMessages(
        @RequestParam(required = false)
        to: Long?,
        @RequestParam(required = false, defaultValue = "0")
        page: Long,
        @RequestParam(required = false, defaultValue = "10")
        size: Long,
        @RequestParam(required = false, defaultValue = "createdDate")
        sort: String,
        @RequestParam(required = false, defaultValue = "DESC")
        dir: String
    ): PagedModel<EntityModel<Message>> {
        val mySendMessages = messageService.getMessagesSendByUser(SendMessagesQuery(to, page, size, sort, dir))
        if (mySendMessages.content.size > 0) {
            pagedResourcesAssembler.toModel(
                mySendMessages,
                linkTo<MessageController> { getAllSendMessages(to, page, size, sort, dir) }.withSelfRel()
            )
        }
        throw MessageNotFoundException("Messages not found")
    }

    @ApiOperation(value = "Api for getting messages received by the user")
    @ApiResponses(
        value = [ApiResponse(code = 200, message = "Successfully sent message to user"),
            ApiResponse(code = 404, message = "Recipient user not found"),
            ApiResponse(code = 403, message = "User not permitted to get the data"),
            ApiResponse(code = 400, message = "invalid data in request")]
    )
    @GetMapping("/received")
    fun getAllReceivedMessages(
        @RequestParam(required = false)
        from: Long?,
        @RequestParam(required = false, defaultValue = "0")
        page: Long,
        @RequestParam(required = false, defaultValue = "10")
        size: Long,
        @RequestParam(required = false, defaultValue = "10")
        sort: String,
        @RequestParam(required = false, defaultValue = "DESC")
        dir: String
    ): PagedModel<EntityModel<Message>> {
        val myReceivedMessages =
            messageService.getMessagesReceivedByUser(ReceiveMessagesQuery(from, page, size, sort, dir))
        if (myReceivedMessages.content.size > 0) {
            return pagedResourcesAssembler.toModel(
                myReceivedMessages,
                linkTo<MessageController> { getAllReceivedMessages(from, page, size, sort, dir) }.withSelfRel()
            )
        }
        throw MessageNotFoundException("Messages not found")
    }
}