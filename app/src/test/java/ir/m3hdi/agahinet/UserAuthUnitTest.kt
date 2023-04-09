package ir.m3hdi.agahinet

import ir.m3hdi.agahinet.data.model.UserSignin
import ir.m3hdi.agahinet.data.model.UserSignup
import ir.m3hdi.agahinet.data.repository.UserAuthRepository
import ir.m3hdi.agahinet.di.ApplicationModule.provideANetService
import ir.m3hdi.agahinet.di.ApplicationModule.provideRetrofit
import ir.m3hdi.agahinet.di.ApplicationModule.provideMoshi
import ir.m3hdi.agahinet.di.ApplicationModule.provideOkHttpClient
import ir.m3hdi.agahinet.di.ApplicationModule.provideUserAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before


class UserAuthUnitTest {


    lateinit var authRepo: UserAuthRepository

    @Before
    fun setUp() {
        authRepo=provideUserAuthRepository(provideANetService(provideRetrofit(provideOkHttpClient(), provideMoshi())),
            Dispatchers.IO)
    }

    @Test
    fun testSignup() = runBlocking {
        val user= UserSignup(fullname = "addhdh", email = "adsegdg@a.com", password = "12fghfh3", phoneNumber = "6546464")
        val res=authRepo.signUp(user)
        assert(res.isSuccess)
        if (res.isSuccess && res.getOrNull()!!.message=="ok"){
            println(res.getOrNull()!!.accessToken!!)
        }
    }

    @Test
    fun testSignin() = runBlocking {
        val user= UserSignin( email = "adsegdg@a.com", password = "12fghfh3", )
        val res=authRepo.signIn(user)
        assert(res.isSuccess)
        if (res.isSuccess && res.getOrNull()!!.message=="ok"){
            println(res.getOrNull()!!.accessToken!!)
        }
    }

}