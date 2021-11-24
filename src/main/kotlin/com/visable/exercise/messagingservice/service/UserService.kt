package com.visable.exercise.messagingservice.service

import com.visable.exercise.messagingservice.controller.dto.MessageUserDto
import com.visable.exercise.messagingservice.controller.dto.toUser
import com.visable.exercise.messagingservice.exception.UserNotFoundException
import com.visable.exercise.messagingservice.model.MessageUser
import com.visable.exercise.messagingservice.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    fun registerUser(userDto: MessageUserDto):MessageUser = userRepository.save(userDto.toUser())

    fun getUser(userId: Long): MessageUser? = userRepository.findById(userId).orElse(null)
}