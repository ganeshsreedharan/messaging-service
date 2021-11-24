package com.visable.exercise.messagingservice.config.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.visable.exercise.messagingservice.config.LoggedInUserData
import com.visable.exercise.messagingservice.constant.MessagingConstants
import com.visable.exercise.messagingservice.constant.MessagingConstants.UNSECURE_URLS
import com.visable.exercise.messagingservice.controller.dto.ErrorMessage
import com.visable.exercise.messagingservice.service.UserService
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
class UserFilter(
    private val userService: UserService,
    private val loggedInUserData: LoggedInUserData,
    private val mapper: ObjectMapper,
) : OncePerRequestFilter() {

    @Throws(ServletException::class)
    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        !(request.requestURI.matches(Regex("/message")) || request.requestURI.matches(Regex("/message/([^\\/]+)")))

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val userId: String? = request.getHeader(MessagingConstants.USER_HEADER)
        if (userId?.isNotBlank() ==  true) {
            val loggedInUser = userService.getUser(userId.toLong())
            loggedInUserData.userDetails = loggedInUser
            filterChain.doFilter(request, response)
        } else {
            handleException(response, "${MessagingConstants.USER_HEADER} is missing")
        }
    }

    private fun handleException(response: HttpServletResponse, message: String) {
        val errorMessage = ErrorMessage(date = Date(), errorCode = "USER_NOT_PERMITTED", message)
        response.sendError(HttpStatus.FORBIDDEN.value(), mapper.writeValueAsString(errorMessage))
    }
}