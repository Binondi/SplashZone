package devs.org.splashzone.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import devs.org.splashzone.SetWallActivity
import devs.org.splashzone.databinding.WallpaperDialogBinding

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: WallpaperDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WallpaperDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeScreen.setOnClickListener {
            setWallpaper(SetWallActivity.WallpaperType.HOME_SCREEN)
            dismiss()
        }

        binding.lockScreen.setOnClickListener {
            setWallpaper(SetWallActivity.WallpaperType.LOCK_SCREEN)
            dismiss()
        }

        binding.both.setOnClickListener {
            setWallpaper(SetWallActivity.WallpaperType.BOTH)
            dismiss()
        }

        binding.close.setOnClickListener {
            dismiss()
        }
    }

    private fun setWallpaper(wallpaperType: SetWallActivity.WallpaperType) {
        val activity = activity as? SetWallActivity
        activity?.let {
            val imageUrl = it.intent.getStringExtra("url") ?: ""
            when (wallpaperType) {
                SetWallActivity.WallpaperType.HOME_SCREEN -> it.setWallpaperForHomeScreen()
                SetWallActivity.WallpaperType.LOCK_SCREEN -> it.setWallpaperForLockScreen()
                SetWallActivity.WallpaperType.BOTH -> it.setWallpaperForBoth()
            }
        }
    }
}

