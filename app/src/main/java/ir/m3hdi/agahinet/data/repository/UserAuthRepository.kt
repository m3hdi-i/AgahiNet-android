package ir.m3hdi.agahinet.data.repository

import ir.m3hdi.agahinet.data.remote.model.UserAuthResponse
import ir.m3hdi.agahinet.data.remote.model.UserSignin
import ir.m3hdi.agahinet.data.remote.model.UserSignup
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.util.AppUtils.Companion.suspendRunCatching
import ir.m3hdi.agahinet.domain.model.Resultx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserAuthRepository @Inject constructor(private val api: ANetService)
{
    suspend fun signUp(signUp: UserSignup): Resultx<UserAuthResponse> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.signup(signUp)
        }
    }

    suspend fun signIn(signIn: UserSignin): Resultx<UserAuthResponse> = withContext(Dispatchers.IO){
        return@withContext suspendRunCatching {
            api.signin(signIn)
        }
    }

}
