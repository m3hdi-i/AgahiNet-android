package ir.m3hdi.agahinet

import ir.m3hdi.agahinet.data.model.AdFilters
import ir.m3hdi.agahinet.data.model.UserSignin
import ir.m3hdi.agahinet.data.model.UserSignup
import ir.m3hdi.agahinet.data.repository.AdRepository
import ir.m3hdi.agahinet.data.repository.UserAuthRepository
import ir.m3hdi.agahinet.di.ApplicationModule.provideANetService
import ir.m3hdi.agahinet.di.ApplicationModule.provideAdsRepository
import ir.m3hdi.agahinet.di.ApplicationModule.provideRetrofit
import ir.m3hdi.agahinet.di.ApplicationModule.provideMoshi
import ir.m3hdi.agahinet.di.ApplicationModule.provideOkHttpClient
import ir.m3hdi.agahinet.di.ApplicationModule.provideUserAuthRepository
import ir.m3hdi.agahinet.util.onFailure
import ir.m3hdi.agahinet.util.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before


class ANetServicesUnitTest {



    lateinit var authRepo: UserAuthRepository
    lateinit var adsRepo: AdRepository

    @Before
    fun setUp() {
        val anetService=provideANetService(provideRetrofit(provideOkHttpClient(), provideMoshi()))
        authRepo=provideUserAuthRepository(anetService, Dispatchers.IO)
        adsRepo=provideAdsRepository(anetService, Dispatchers.IO)
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

    @Test
    fun testSearchAds(): Unit = runBlocking {
        val filters= AdFilters(null,null,null,null,null,10,0 )
        adsRepo.searchAds(filters).onFailure {
            println(it.stackTraceToString())
            assert(false)
        }.onSuccess {
            it.forEach {ad->
                println(ad.title)
            }
            assert(true)
        }

    }


}