package com.visable.exercise.messagingservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter


//Completely optional configuration, Every time user won't get any messages(sent/received) ,so we can restrict some unwanted get apis
@Configuration
class EtagConfiguration {

    @Bean
    fun shallowEtagHeaderFilter(): ShallowEtagHeaderFilter = ShallowEtagHeaderFilter()
}