package devs.org.splashzone

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.database.FirebaseDatabase
import devs.org.splashzone.Fragments.BottomSheetFragment
import devs.org.splashzone.databinding.ActivitySetWallBinding

class SetWallActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetWallBinding
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference.child("wallpapers")

    enum class WallpaperType {
        HOME_SCREEN,
        LOCK_SCREEN,
        BOTH
    }

    fun setWallpaperForHomeScreen() {
        setHomeScreenWallpaper()
    }

    fun setWallpaperForLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLockScreenWallpaper()
        }
    }

    fun setWallpaperForBoth() {
        setWallpaper()
        showToast("Wallpaper set for Home and Lock Screens.")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetWallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl: String? = intent.getStringExtra("url")
        val name: String? = intent.getStringExtra("name")
        val downloads = intent.getLongExtra("down", 0)
        val uid : String? = intent.getStringExtra("uid")

        binding.name.text = name

        var dumyDowload = downloads

        if (dumyDowload > 999) {
            dumyDowload /= 1000
            binding.downloads.text = "$dumyDowload k Downloads"
        } else {
            binding.downloads.text = "$dumyDowload Downloads"
        }

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.click_bg)
            .into(binding.wallpaper)


        statusBarColor()
        uid?.let { setClickListeners(it, downloads) }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun statusBarColor() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    private fun showBottomSheet(){
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)

    }

    private fun setClickListeners(uid:String, downloads:Long) {
        binding.set.setOnClickListener {
            showBottomSheet()
        }

        binding.download.setOnClickListener {
            downloadImage()
            updateDownload(uid, downloads)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDownload(uid:String, downloads:Long){
        reference.child(uid).child("downloads").setValue(downloads + 1).addOnCompleteListener { task->
            if (task.isSuccessful){
                var finalDownloads = downloads +1
                if (finalDownloads > 999) {
                    finalDownloads /= 1000
                    binding.downloads.text = "$finalDownloads k Downloads"
                } else {
                    binding.downloads.text = "$finalDownloads Downloads"
                }
            }
        }
    }

    private fun downloadImage() {
        val imageUrl: String? = intent.getStringExtra("url")
        val fileName = "SplashZone_${System.currentTimeMillis()}.jpg"

        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    saveImageToGallery(resource, fileName)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun saveImageToGallery(bitmap: Bitmap, fileName: String) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
            contentResolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                showToast("Image downloaded and saved.")
            }
        } ?: showToast("Error saving image to gallery.")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun setWallpaper() {
        val wallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable = binding.wallpaper.drawable
        if (wallpaperDrawable is BitmapDrawable) {
            val wallpaperBitmap: Bitmap = wallpaperDrawable.bitmap

            try {
                wallpaperManager.setBitmap(wallpaperBitmap)
                showToast("Wallpaper set successfully.")
            } catch (e: Exception) {
                showToast("Error setting wallpaper: ${e.message}")
            }
        } else {
            showToast("Error setting wallpaper: Wallpaper is not a BitmapDrawable.")
        }
    }

    private fun setHomeScreenWallpaper() {
        WallpaperManager.getInstance(this)
        val wallpaperDrawable = binding.wallpaper.drawable
        if (wallpaperDrawable is BitmapDrawable) {
            val wallpaperBitmap: Bitmap = wallpaperDrawable.bitmap

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setWallpaperForType(wallpaperBitmap, WallpaperManager.FLAG_SYSTEM)
                }
                showToast("Wallpaper set for Home Screen.")
            } catch (e: Exception) {
                showToast("Error setting home screen wallpaper: ${e.message}")
            }
        } else {
            showToast("Error setting home screen wallpaper: Wallpaper is not a BitmapDrawable.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLockScreenWallpaper() {
        WallpaperManager.getInstance(this)
        val wallpaperDrawable = binding.wallpaper.drawable
        if (wallpaperDrawable is BitmapDrawable) {
            val wallpaperBitmap: Bitmap = wallpaperDrawable.bitmap

            try {
                setWallpaperForType(wallpaperBitmap, WallpaperManager.FLAG_LOCK)
                showToast("Wallpaper set for Lock Screen.")
            } catch (e: Exception) {
                showToast("Error setting lock screen wallpaper: ${e.message}")
            }
        } else {
            showToast("Error setting lock screen wallpaper: Wallpaper is not a BitmapDrawable.")
        }
    }

    private fun setWallpaperForType(bitmap: Bitmap, wallpaperType: Int) {
        val wallpaperManager = WallpaperManager.getInstance(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            wallpaperManager.setBitmap(bitmap, null, true, wallpaperType)
        }
    }
}
