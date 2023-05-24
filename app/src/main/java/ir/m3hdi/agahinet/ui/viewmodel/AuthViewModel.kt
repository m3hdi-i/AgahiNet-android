package ir.m3hdi.agahinet.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.m3hdi.agahinet.data.model.SigninState
import ir.m3hdi.agahinet.data.model.SignupState
import ir.m3hdi.agahinet.data.model.UserSignin
import ir.m3hdi.agahinet.data.model.UserSignup
import ir.m3hdi.agahinet.data.repository.UserAuthRepository
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.AppUtils.Companion.saveSuccessfulAuthData
import ir.m3hdi.agahinet.data.Resultx
import ir.m3hdi.agahinet.data.onFailure
import ir.m3hdi.agahinet.data.onSuccess
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userAuthRepository: UserAuthRepository,application: Application) : AndroidViewModel(application)  {

    private val _signinState = MutableLiveData<Resultx<SigninState>>()
    val signinState: LiveData<Resultx<SigninState>> get() = _signinState

    private val _signupState = MutableLiveData<Resultx<SignupState>>()
    val signupState: LiveData<Resultx<SignupState>> get() = _signupState

    fun performSignin(email:String,password:String)
    {
        val validEmail=AppUtils.isValidEmail(email)
        val validPassword= password.isNotBlank()

        if (!validEmail){
            _signinState.value= Resultx.success(SigninState.BAD_EMAIL)
            return
        }
        if (!validPassword){
            _signinState.value = Resultx.success(SigninState.BAD_PASSWORD)
            return
        }
        _signinState.value= Resultx.loading()

        viewModelScope.launch {
            userAuthRepository.signIn(UserSignin(email, password)).onSuccess {
                val ok=it.message=="ok"
                if (ok){
                    _signinState.value= Resultx.success(SigninState.OK)
                    saveSuccessfulAuthData(getApplication<Application>().applicationContext,it)

                }else{
                    _signinState.value= Resultx.success(SigninState.INCORRECT_CREDS)
                }


            }.onFailure {
                _signinState.value= Resultx.failure(it)
            }
        }
    }

    fun performSignup(fullname:String, email:String, password: String,phoneNumber:String){

        val validName= fullname.isNotBlank()
        val validEmail=AppUtils.isValidEmail(email)
        val validPassword= password.isNotBlank() && password.length >= 6
        val validPhoneNumber= phoneNumber.isNotBlank()

        if (!validName){
            _signupState.value= Resultx.success(SignupState.BAD_NAME)
            return
        }
        if (!validEmail){
            _signupState.value= Resultx.success(SignupState.BAD_EMAIL)
            return
        }
        if (!validPassword){
            _signupState.value= Resultx.success(SignupState.BAD_PASSWORD)
            return
        }
        if (!validPhoneNumber){
            _signupState.value= Resultx.success(SignupState.BAD_PHONE_NUMBER)
            return
        }

        _signupState.value= Resultx.loading()

        viewModelScope.launch {
            userAuthRepository.signUp(UserSignup(fullname,email,password,phoneNumber)).onSuccess {
                val ok=it.message=="ok"
                if (ok){
                    _signupState.value= Resultx.success(SignupState.OK)
                    saveSuccessfulAuthData(getApplication<Application>().applicationContext,it)

                }else{
                    _signupState.value= Resultx.success(SignupState.DUPLICATE_EMAIL)
                }
            }.onFailure {
                _signupState.value= Resultx.failure(it)
            }
        }

    }

 /*   fun aaa(){
        viewModelScope.launch {
            val result1 = async { f1() }
            val result2 = async { f2() }
            aa.value = listOf(result1.await(), result2.await())
        }
    }*/
}