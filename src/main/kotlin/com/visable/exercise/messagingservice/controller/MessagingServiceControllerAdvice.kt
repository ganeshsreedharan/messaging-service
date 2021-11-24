package com.visable.exercise.messagingservice.controller

import com.visable.exercise.messagingservice.controller.dto.ErrorMessage
import com.visable.exercise.messagingservice.exception.UserNotFoundException
import com.visable.exercise.messagingservice.exception.UserNotPermittedException
import org.hibernate.exception.ConstraintViolationException
import org.slf4j.LoggerFactory.getLogger
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import java.util.Date

@ControllerAdvice
@RestController
class MessagingServiceControllerAdvice {

    companion object {
        private val log
                = getLogger(MessagingServiceControllerAdvice::class.java)
    }

    @ExceptionHandler(value = [UserNotFoundException::class])
    @ResponseBody
    fun handleUserNotFoundException(ex: UserNotFoundException, request: WebRequest): ResponseEntity<ErrorMessage> {
        log.error("User not found exception happened :  ${ex.message } ")
        val error = ErrorMessage(Date(),"USER_NOT_FOUND",message = ex.message!!)
        return ResponseEntity(error,HttpStatus.NOT_FOUND) // Also, can use HttpStatus.NO_CONTENT
    }

    @ExceptionHandler(value = [UserNotPermittedException::class])
    @ResponseBody
    fun handleUserNotPermittedException(ex: UserNotPermittedException, request: WebRequest): ResponseEntity<ErrorMessage> {
        log.error("User not permitted exception happened :  ${ex.message } ")
        val error = ErrorMessage(Date(),"USER_NOT_PERMITTED",message = ex.message!!)
        return ResponseEntity(error,HttpStatus.FORBIDDEN)
    }


    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseBody
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<Any> {
        log.error("Invalid Data in request :  ${ex.message } ")
        val error = ErrorMessage(Date(),"INVALID_REQUEST_DATA",message = ex.fieldError?.defaultMessage!!)
        return ResponseEntity(error,HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    fun handleException(ex: Exception): ResponseEntity<ErrorMessage> {
        log.error("exception Happened :  ${ex.message } ")
        if(ex is IllegalArgumentException && ex.message!!.contains("Invalid UUID") ){
            val error = ErrorMessage(Date(),"INVALID_ID",message = "invalid uuid in request")
            return ResponseEntity(error,HttpStatus.BAD_REQUEST)
        }
        val error = ErrorMessage(Date(),"SERVER_ERROR",message = "Something went wrong !")
        return ResponseEntity(error,HttpStatus.INTERNAL_SERVER_ERROR)

    }

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    @ResponseBody
    fun handleConstraintViolationException(ex: DataIntegrityViolationException): ResponseEntity<ErrorMessage> {
        log.error("constraint exception Happened :  ${ex.message} ")
        if(ex.cause is ConstraintViolationException) {
            //TODO: now handling only unique constrain violation exception for the nick name entry in message_user entity
            val error =
                ErrorMessage(Date(), "RESOURCE_ALREADY_FOUND", message = "User with same nickname already exists")
            return ResponseEntity(error, HttpStatus.CONFLICT)
        }
        val error = ErrorMessage(Date(),"SERVER_ERROR",message = "Something went wrong !")
        return ResponseEntity(error,HttpStatus.INTERNAL_SERVER_ERROR)

    }
}