package com.visable.exercise.messagingservice.config

import com.visable.exercise.messagingservice.model.MessageUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.annotation.RequestScope

@Configuration
class MessagingServiceConfig {

    @Bean
    @RequestScope
    fun loggedInUserData():LoggedInUserData = LoggedInUserData(null);
}

open class LoggedInUserData(var userDetails: MessageUser?)