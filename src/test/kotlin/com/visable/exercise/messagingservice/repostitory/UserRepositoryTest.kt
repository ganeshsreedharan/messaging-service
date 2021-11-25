package com.visable.exercise.messagingservice.repostitory

import com.visable.exercise.messagingservice.common.DataBaseInitializer
import com.visable.exercise.messagingservice.model.MessageUser
import com.visable.exercise.messagingservice.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository
) : DataBaseInitializer() {

    lateinit var userA: MessageUser
    lateinit var userB: MessageUser

    @Test
    fun `when creating chat user`(){
        userA = userRepository.save(MessageUser(nickName = "userA", createdDate = LocalDateTime.now()))

        assertThat(userRepository.count() > 0)
        assertThat(userRepository.findById(userA.userId!!).get().nickName == userA.nickName)

    }

    @Test
    fun `when creating chat user with duplicate nickname`(){
     assertThrows<DataIntegrityViolationException> {
            userA = userRepository.save(MessageUser(nickName = "userA", createdDate = LocalDateTime.now()))

            userB = userRepository.save(MessageUser(nickName = "userA", createdDate = LocalDateTime.now()))

        }

    }

}