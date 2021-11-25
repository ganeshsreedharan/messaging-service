package com.visable.exercise.messagingservice.repository

import com.visable.exercise.messagingservice.model.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

interface MessageRepository: PagingAndSortingRepository<Message, Long> {
    fun findAllBySenderUserId(id:Long, pageable: Pageable): Page<Message>
    fun findAllByReceiverUserId(id:Long,pageable: Pageable):Page<Message>
    fun findAllBySenderUserIdAndReceiverUserId(senderId:Long, receiverId:Long, pageable: Pageable):Page<Message>
}