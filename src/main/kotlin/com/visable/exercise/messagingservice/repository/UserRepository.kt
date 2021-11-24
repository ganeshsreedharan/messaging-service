package com.visable.exercise.messagingservice.repository

import com.visable.exercise.messagingservice.model.MessageUser
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<MessageUser,Long>