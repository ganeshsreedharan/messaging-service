package com.visable.exercise.messagingservice.exception


class UserNotFoundException(message: String) : RuntimeException(message)

class UserNotPermittedException(message: String) : RuntimeException(message)

