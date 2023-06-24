package ir.m3hdi.agahinet.util

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.transition.TransitionManager
import com.github.yamin8000.ppn.PersianHelpers.spellToPersian
import com.google.android.material.transition.MaterialSharedAxis
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import ir.m3hdi.agahinet.domain.model.Resultx
import ir.m3hdi.agahinet.data.remote.model.User
import ir.m3hdi.agahinet.data.remote.model.UserAuthResponse
import ir.m3hdi.agahinet.domain.model.AuthedUser
import ir.m3hdi.agahinet.ui.fragment.NeedAuthFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.cancellation.CancellationException


class AppUtils {

    companion object {

        const val BASE_URL="http://10.0.3.2:8000/"

        val currentUser= MutableStateFlow<AuthedUser?>(null)

        fun saveSuccessfulAuthData(context: Context,auth: UserAuthResponse)
        {
            if (auth.user!=null && auth.accessToken!=null){

                currentUser.value = AuthedUser(auth.accessToken, auth.user)

                auth.user.let {
                    getSecureSharedPref(context).edit().apply{
                        putString("uid",it.uid)
                        putString("fullname",it.fullname)
                        putString("email",it.email)
                        putString("phone_number",it.phoneNumber)
                        putString("jwt",auth.accessToken)

                    }.apply()
                }

            }
        }

        fun retrieveAuthData(context: Context)
        {
            val ssp = getSecureSharedPref(context)
            val savedJwt = ssp.getString("jwt",null)
            savedJwt?.let {jwt->
                val user =  User(
                    uid = ssp.getString("uid",null) ?: "",
                    fullname =  ssp.getString("fullname",null) ?: "",
                    email = ssp.getString("email",null) ?: "",
                    phoneNumber =  ssp.getString("phone_number",null) ?: "",
                )

                currentUser.value = AuthedUser(jwt,user)
            }
        }


        fun signOutUser(context: Context) {

            currentUser.value = null

            val secureSharedP=getSecureSharedPref(context)
            secureSharedP.edit().apply{
                remove("jwt")
                remove("uid")
                remove("fullname")
                remove("email")
                remove("phone_number")
            }.apply()

        }


        fun handleNeedAuthFragment(authed:Boolean,fragmentManager: FragmentManager, layoutParent: ViewGroup, layoutNeedAuth: View, layoutContent: View){

            val tag="need_auth_${layoutParent.id}"
            if (authed){
                val fragment = fragmentManager.findFragmentByTag(tag)
                fragment?.let {
                    val transaction= fragmentManager.beginTransaction()
                    transaction.remove(it)
                    transaction.commit()
                }
                sharedAxisYTransition(layoutParent,layoutNeedAuth,layoutContent)

            }
            else{
                if (fragmentManager.findFragmentByTag(tag)==null){
                    val fragment= NeedAuthFragment()
                    val transaction= fragmentManager.beginTransaction()
                    transaction.replace(layoutNeedAuth.id,fragment,tag)
                    transaction.commit()
                }
                sharedAxisYTransition(layoutParent,layoutContent,layoutNeedAuth)
            }
        }

        fun isValidEmail(email: String): Boolean {
            return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun showWithsharedAxisYTransition(container: ViewGroup, inView: View)
        {
            val sharedAxis = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            TransitionManager.beginDelayedTransition(container, sharedAxis)

            inView.visibility= View.VISIBLE
        }
        fun hideWithsharedAxisYTransition(container: ViewGroup, outView: View)
        {
            val sharedAxis = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            TransitionManager.beginDelayedTransition(container, sharedAxis)

            outView.visibility= View.GONE
        }

        fun sharedAxisYTransition(container: ViewGroup, outView: View, inView: View)
        {
            val sharedAxis = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            TransitionManager.beginDelayedTransition(container, sharedAxis)

            outView.visibility= View.GONE
            inView.visibility= View.VISIBLE
        }

        fun sharedAxisXTransition(container: ViewGroup, outView: View, inView: View)
        {
            val sharedAxis = MaterialSharedAxis(MaterialSharedAxis.X, true)
            TransitionManager.beginDelayedTransition(container, sharedAxis)

            outView.visibility= View.GONE
            inView.visibility= View.VISIBLE
        }

        fun hasInternetConnection(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when{
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
            else{
                connectivityManager.activeNetworkInfo?.run {
                    return when(type){
                        ConnectivityManager.TYPE_WIFI -> return true
                        ConnectivityManager.TYPE_MOBILE -> return true
                        ConnectivityManager.TYPE_ETHERNET -> return true
                        else -> false
                    }
                }
            }
            return false
        }

        fun getSecureSharedPref(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(context,
                "secure_shared_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        /**
         * Like [runCatching], but with proper coroutines cancellation handling. Also only catches [Exception] instead of [Throwable]
         * Cancellation exceptions need to be rethrown. See https://github.com/Kotlin/kotlinx.coroutines/issues/1814
         */
        suspend fun <T> suspendRunCatching(block: suspend () -> T): Resultx<T> = try {
            Resultx.success(block())
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            Resultx.failure(exception)
        }

        fun getMaterialSharedAxisZTransition(forward:Boolean,excludeViewId:Int?=null): MaterialSharedAxis {
            return MaterialSharedAxis(MaterialSharedAxis.Z, forward).apply {
                excludeViewId?.let {
                    excludeTarget(excludeViewId,true)
                }
            }
        }

        private const val currencySuffix=" تومان"
        fun priceToPersianCurrencyLetters(text:String?) = if (text.isNullOrBlank()) ""  else text.toString().spellToPersian() + currencySuffix

        fun formatPrice(price:String?):String{
            if (!price.isNullOrBlank()){
                return String.format("%,d",price.toLongOrNull() ?: 0L) + currencySuffix
            }
            return "توافقی"
        }

        fun getImageUrlByImageId(imageId:Int) = "${BASE_URL}api/image?image_id=${imageId}"


        fun <T:Any> Observable<T>.ioOnUi():Observable<T>{
            return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }

        fun dpToPx(context: Context, dp: Int) = (dp * context.resources.displayMetrics.density).toInt()

    }
}

/**
 * The mutable version of kotlin's [Pair]
 */
class MutablePair<A, B>(var first: A, var second: B)

