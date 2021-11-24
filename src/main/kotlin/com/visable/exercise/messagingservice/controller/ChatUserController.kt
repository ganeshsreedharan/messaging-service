package com.visable.exercise.messagingservice.controller

import com.visable.exercise.messagingservice.controller.dto.MessageUserDto
import com.visable.exercise.messagingservice.controller.dto.ResponseDto
import com.visable.exercise.messagingservice.exception.UserNotFoundException
import com.visable.exercise.messagingservice.model.MessageUser
import com.visable.exercise.messagingservice.service.UserService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/chat-user")
class ChatUserController(private val userService: UserService) {

    @ApiOperation(value = "Api for on boarding  a new chat user in the system by giving their nick name" )
    @ApiResponses(value = [ ApiResponse(code = 201,message = "Successfully onboarded  new  user"),
        ApiResponse(code = 409,message = "User with same nick name exists in the system"),
        ApiResponse(code = 400,message = "invalid nick name value")])
    @PostMapping
    fun onBoardUser(@Valid @RequestBody userDto: MessageUserDto):ResponseEntity< ResponseDto<String>> {
        val savedUser = userService.registerUser(userDto)
        val responseDto = ResponseDto("Chat user saved successfully")
        responseDto.add(linkTo<ChatUserController> { getChatUser(savedUser.userId!!) }.withSelfRel())
        return ResponseEntity(responseDto, HttpStatus.CREATED)
    }

    @ApiOperation(value = "Api for getting onboarded user by user id" )
    @ApiResponses(value = [ ApiResponse(code = 200,message = "Successfully fetched user"),
        ApiResponse(code = 400,message = "invalid user user id in request"),
        ApiResponse(code = 404,message = "user not found")])
    @GetMapping("{userId}")
    fun getChatUser(@ApiParam("user id") @PathVariable userId: Long): ResponseDto<MessageUser> {
        val userInfo = userService.getUser(userId)
        userInfo?.let {
            return  ResponseDto(content =it)
        }
        throw UserNotFoundException("user with use id :$userId is not found")

    }

}