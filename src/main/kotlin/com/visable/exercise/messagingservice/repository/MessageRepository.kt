package com.visable.exercise.messagingservice.repository

import com.visable.exercise.messagingservice.model.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository:JpaRepository<Message,Long>{
    fun findAllBySenderUserId(id:Long, pageable: Pageable): Page<Message>
    fun findAllByReceiverUserId(id:Long,pageable: Pageable):Page<Message>
    fun findAllBySenderUserIdOrReceiverUserId(senderId:Long, receiverId:Long, pageable: Pageable):Page<Message>
    fun findAllBySenderUserIdAndReceiverUserId(senderId:Long, receiverId:Long, pageable: Pageable):Page<Message>
}