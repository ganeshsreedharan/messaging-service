package com.visable.exercise.messagingservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.ShallowEtagHeaderFilter


@Configuration
class EtagConfiguration {

    @Bean
    fun shallowEtagHeaderFilter(): ShallowEtagHeaderFilter = ShallowEtagHeaderFilter()
}