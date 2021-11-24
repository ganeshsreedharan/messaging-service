package com.visable.exercise.messagingservice.model;
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "Message")
class Message(
    @Id @GeneratedValue var messageId: Long? = null,
    @OneToOne  @JoinColumn(name = "sender_id")
    val sender: MessageUser,
    @OneToOne @JoinColumn(name = "receiver_id" )
    val receiver: MessageUser,
    val messageContent: String,
    val createdDate: Date
)
fun Message.toMessagePayload() = MessagePayload(
    sender = sender.nickName,
    receiver = receiver.nickName,
    messageContent = messageContent
)

class MessagePayload(
    val sender: String,
    val receiver: String,
    val messageContent: String
):Serializable
