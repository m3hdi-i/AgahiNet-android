package ir.m3hdi.agahinet.domain.model

import ir.m3hdi.agahinet.data.remote.model.User

data class AuthedUser(val jwt:String, val user:User)
