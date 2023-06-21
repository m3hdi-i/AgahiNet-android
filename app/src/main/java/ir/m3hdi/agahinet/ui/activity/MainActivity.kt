package ir.m3hdi.agahinet.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.toWindowInsetsCompat
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.ActivityMainBinding
import ir.m3hdi.agahinet.util.AppUtils.Companion.retrieveAuthData

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration:AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        binding.navView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_screen,
                R.id.new_ad_screen,
                R.id.chat_screen,
                R.id.user_screen
            )
        )

        retrieveAuthData(applicationContext)

        // Hide BottomNavigationView when keyboard appears
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = toWindowInsetsCompat(insets, view)
            binding.navView.isGone = insetsCompat.isVisible(ime())
            view.onApplyWindowInsets(insets)
        }

        // Hide keyborad if input view loses focus
        /*val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        window.decorView.viewTreeObserver.addOnGlobalFocusChangeListener { oldView, newView ->
            if (newView !is TextInputEditText)
                imm.hideSoftInputFromWindow((oldView ?: newView)?.windowToken ?: window.attributes.token, 0)
        }*/

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}