package ir.m3hdi.agahinet.data.repository

import ir.m3hdi.agahinet.data.model.UserAuthResponse
import ir.m3hdi.agahinet.data.model.UserSignin
import ir.m3hdi.agahinet.data.model.UserSignup
import ir.m3hdi.agahinet.data.remote.ANetService
import ir.m3hdi.agahinet.util.AppUtils.Companion.suspendRunCatching
import ir.m3hdi.agahinet.util.Resultx
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserAuthRepository @Inject constructor(private val api: ANetService,private val ioDispatcher: CoroutineDispatcher)
{
    suspend fun signUp(signUp: UserSignup):Resultx<UserAuthResponse> = withContext(ioDispatcher){
        return@withContext suspendRunCatching {
            api.signup(signUp)
        }
    }

    suspend fun signIn(signIn: UserSignin):Resultx<UserAuthResponse> = withContext(ioDispatcher){
        return@withContext suspendRunCatching {
            api.signin(signIn)
        }
    }

}
