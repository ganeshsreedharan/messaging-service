package com.visable.exercise.messagingservice.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "MessageUser")
class MessageUser(
    @Id @GeneratedValue  var userId: Long? = null,
    @Column(unique = true) val nickName: String,
    @JsonIgnore
    @Column(nullable = false) val createdDate: LocalDateTime
)