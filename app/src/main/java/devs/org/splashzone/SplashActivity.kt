package devs.org.splashzone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val handler = Handler(mainLooper)
        statusBar()

        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        },2000)
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun statusBar(){
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        val defaultBackgroundColor = typedValue.data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = defaultBackgroundColor
        }
    }
}