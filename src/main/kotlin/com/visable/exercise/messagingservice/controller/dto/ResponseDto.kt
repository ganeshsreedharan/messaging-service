package com.visable.exercise.messagingservice.controller.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.hateoas.RepresentationModel
import java.util.Date

open class ResponseDto<T>( @JsonProperty("data") val content:T) : RepresentationModel<ResponseDto<T>>()

open class ErrorMessage(var date: Date, var errorCode:String, var message: String)


