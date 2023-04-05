package ir.m3hdi.agahinet.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.transition.TransitionManager
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialContainerTransform
import ir.m3hdi.agahinet.databinding.ActivityAuthBinding
import ir.m3hdi.agahinet.util.AppUtils

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()

        if (AppUtils.getJWT(applicationContext) != null){
            // Go to user desired fragment
        }
    }

    private fun setUpUI()
    {
        val transform = MaterialContainerTransform().apply {
            // Manually tell the container transform which Views to transform between.
            startView = binding.layoutSignin
            endView = binding.layoutSignup
            containerColor= Color.parseColor("#FAFAFA")
            // Ensure the container transform only runs on a single target
            addTarget(endView as ConstraintLayout)

            // Optionally add a curved path to the transform
            //pathMotion = MaterialArcMotion()

            // Since View to View transforms often are not transforming into full screens,
            // remove the transition's scrim.
            scrimColor = Color.parseColor("#FAFAFA")
        }


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
            binding.layoutSignin.visibility= View.GONE
            binding.layoutSignup.visibility= View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.layoutSignin, transform)

        }
        binding.buttonBackToSignin.setOnClickListener {
            binding.layoutSignup.visibility= View.GONE
            binding.layoutSignin.visibility= View.VISIBLE
        }

        binding.buttonCancel.setOnClickListener { finish() }




    }
}