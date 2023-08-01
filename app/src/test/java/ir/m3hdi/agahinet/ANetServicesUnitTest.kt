package ir.m3hdi.agahinet


class ANetServicesUnitTest {


    /*
    lateinit var authRepo: UserAuthRepository
    lateinit var adsRepo: AdRepository

    @Before
    fun setUp() {
        val anetService=provideANetService(provideRetrofit(provideOkHttpClient(), provideMoshi()))
        authRepo=provideUserAuthRepository(anetService)
        adsRepo=provideAdsRepository(anetService)
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
        val filters= SearchFilters(null,null,null,null,null,10,0 )
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

*/

}