package com.kotlin.test.constant.exception

import javax.persistence.PersistenceException

object ExceptionConstant {

    val MEMBER_ALREADY_EXISTS_EXCEPTION = IllegalArgumentException(ExceptionMessageConstant.MEMBER_ALREADY_EXISTS_MESSAGE)
    val MEMBER_NOT_FOUND_EXCEPTION = IllegalArgumentException(ExceptionMessageConstant.MEMBER_NOT_FOUND_MESSAGE)

    val PERSISTENCE_EXCEPTION = PersistenceException()
}