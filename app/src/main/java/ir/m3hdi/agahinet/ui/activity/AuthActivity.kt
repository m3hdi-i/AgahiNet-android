package ir.m3hdi.agahinet.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.model.SigninState
import ir.m3hdi.agahinet.data.model.SignupState
import ir.m3hdi.agahinet.databinding.ActivityAuthBinding
import ir.m3hdi.agahinet.ui.viewmodel.AuthViewModel
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.data.Resultx

@AndroidEntryPoint
class AuthActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
    }

    private fun setUpUI()
    {
        binding.editTextSigninPassword.doOnTextChanged{ text, _, _, _ ->
            if (text.toString().isEmpty()){
                binding.textFieldSigninPassword.endIconMode= TextInputLayout.END_ICON_NONE
            }else{
                binding.textFieldSigninPassword.endIconMode= TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
        }
        binding.editTextSignupPassword.doOnTextChanged{ text, _, _, _ ->
            if (text.toString().isEmpty()){
                binding.textFieldSignupPassword.endIconMode= TextInputLayout.END_ICON_NONE
            }else{
                binding.textFieldSignupPassword.endIconMode= TextInputLayout.END_ICON_PASSWORD_TOGGLE
            }
        }

        binding.buttonGotoSignup.setOnClickListener {
            AppUtils.sharedAxisXTransition(binding.materialCardView,binding.layoutSignin,binding.layoutSignup)
        }
        binding.buttonBackToSignin.setOnClickListener {
            AppUtils.sharedAxisXTransition(binding.materialCardView,binding.layoutSignup,binding.layoutSignin)
        }

        binding.topAppBar.setNavigationOnClickListener{
            finish()
        }

        binding.buttonSignin.setOnClickListener {
            if (AppUtils.hasInternetConnection(applicationContext)){
                val email=binding.editTextSigninEmail.text.toString()
                val password=binding.editTextSigninPassword.text.toString()
                viewModel.performSignin(email,password)
            }else{
                Toasty.warning(applicationContext, getString(R.string.no_network), Toast.LENGTH_SHORT).show()
            }

        }


        binding.buttonSignup.setOnClickListener {
            if (AppUtils.hasInternetConnection(applicationContext)){
                val name=binding.editTextSignupName.text.toString()
                val email=binding.editTextSignupEmail.text.toString()
                val password=binding.editTextSignupPassword.text.toString()
                val phoneNumber=binding.editTextSignupPhoneNumber.text.toString()

                viewModel.performSignup(name,email,password,phoneNumber)
            }else{
                Toasty.warning(applicationContext, getString(R.string.no_network), Toast.LENGTH_SHORT).show()
            }

        }

        viewModel.signinState.observe(this){

            when(it){
                is Resultx.Loading -> {
                    AppUtils.sharedAxisYTransition(binding.materialCardView,binding.buttonSignin,binding.progressBarSignin)
                }
                is Resultx.Success -> {
                    AppUtils.sharedAxisYTransition(binding.materialCardView,binding.progressBarSignin,binding.buttonSignin)
                    when(it.value){
                        SigninState.OK -> {
                            Toasty.success(applicationContext, getString(R.string.signin_ok), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        SigninState.BAD_EMAIL -> {
                            Toasty.warning(applicationContext, getString(R.string.bad_email), Toast.LENGTH_SHORT).show()
                        }
                        SigninState.BAD_PASSWORD -> {
                            Toasty.warning(applicationContext, getString(R.string.bad_password), Toast.LENGTH_SHORT).show()
                        }
                        SigninState.INCORRECT_CREDS -> {
                            Toasty.error(applicationContext, getString(R.string.incorrect_creds), Toast.LENGTH_SHORT,).show()
                        }
                    }
                }
                is Resultx.Failure -> {
                    AppUtils.sharedAxisYTransition(binding.materialCardView,binding.progressBarSignin,binding.buttonSignin)
                    Toasty.error(applicationContext, getString(R.string.network_error), Toast.LENGTH_SHORT,false).show()
                }
            }
        }

        viewModel.signupState.observe(this){

            when(it){
                is Resultx.Loading -> {
                    AppUtils.sharedAxisYTransition(binding.materialCardView,binding.buttonSignup,binding.progressBarSignup)
                }
                is Resultx.Success -> {
                    AppUtils.sharedAxisYTransition(binding.materialCardView,binding.progressBarSignup,binding.buttonSignup)
                    when(it.value){
                        SignupState.OK -> {
                            Toasty.success(applicationContext, getString(R.string.signup_ok), Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        SignupState.BAD_NAME -> {
                            Toasty.warning(applicationContext, getString(R.string.bad_name), Toast.LENGTH_SHORT).show()
                        }
                        SignupState.BAD_EMAIL -> {
                            Toasty.warning(applicationContext, getString(R.string.bad_email), Toast.LENGTH_SHORT).show()
                        }
                        SignupState.BAD_PASSWORD -> {
                            Toasty.warning(applicationContext, getString(R.string.bad_password), Toast.LENGTH_SHORT).show()
                        }
                        SignupState.BAD_PHONE_NUMBER -> {
                            Toasty.warning(applicationContext, getString(R.string.bad_phone_number), Toast.LENGTH_SHORT).show()
                        }
                        SignupState.DUPLICATE_EMAIL -> {
                            Toasty.warning(applicationContext, getString(R.string.duplicate_email), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is Resultx.Failure -> {
                    AppUtils.sharedAxisYTransition(binding.materialCardView,binding.progressBarSignup,binding.buttonSignup)
                    Toasty.error(applicationContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}