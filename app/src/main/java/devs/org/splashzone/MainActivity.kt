package devs.org.splashzone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import devs.org.splashzone.Fragments.CategoryFragment
import devs.org.splashzone.Fragments.HomeFragment
import devs.org.splashzone.Fragments.TrendingFragment
import devs.org.splashzone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBar()
        replaceFragment(HomeFragment())

        binding.settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        //bottom navigation
        binding.bottomNav.setOnItemSelectedListener {

            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.trending -> replaceFragment(TrendingFragment())
                R.id.category -> replaceFragment(CategoryFragment())
                else-> {

                }
            }
            true
        }
    }

    //change navigation
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransition = fragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.navHostFragment,fragment)
        fragmentTransition.commit()
    }

    //Matching status bar with background color of the app
    @SuppressLint("ObsoleteSdkInt")
    private fun statusBar(){
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        val defaultBackgroundColor = typedValue.data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = defaultBackgroundColor
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedItemId", binding.bottomNav.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val selectedItemId = savedInstanceState.getInt("selectedItemId")
        binding.bottomNav.selectedItemId = selectedItemId
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        MaterialAlertDialogBuilder(this)
            .setTitle("Exit SplashZone")
            .setMessage("Do You Want to Exit The App ?")
            .setPositiveButton("Yes,Exit") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("Cancel") { _, _ ->
            }
            .show()
    }

}