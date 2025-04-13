package com.hfad.ismlarmanosi2023

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hfad.ismlarmanosi2023.databinding.ActivityMainBinding
import com.hfad.ismlarmanosi2023.databinding.BottomSheetBannerBinding
import com.hfad.ismlarmanosi2023.databinding.BottomSheetUpdateBinding
import com.hfad.ismlarmanosi2023.language.MyContextWrapper
import com.hfad.ismlarmanosi2023.language.MyPreference
import com.hfad.ismlarmanosi2023.remote.Constants
import com.hfad.ismlarmanosi2023.remote.Resource
import com.hfad.ismlarmanosi2023.remote.model.BannerModel
import com.hfad.ismlarmanosi2023.remote.model.RemoteConfigModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var myPreference: MyPreference

    private val sharedViewModel: MainViewModel by viewModels()

    private var isUpdateBannerShown = false

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("AppLogging", "App started")
        MobileAds.initialize(this) {}

        setupToolbarAndNavigation()
        fetchRemoteConfigAndUpdate()
    }

    public override fun attachBaseContext(newbase: Context?) {
        myPreference = MyPreference(newbase!!)
        val lang: String? = myPreference.getLoginCount()
        super.attachBaseContext(MyContextWrapper.wrap(newbase, lang!!))
    }

    fun setupToolbarAndNavigation() {

        val toolbar: MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val drawer: DrawerLayout = findViewById(R.id.lo_drawer)
        val builder = AppBarConfiguration.Builder(navController.graph)
        builder.setOpenableLayout(drawer)
        val appBarConfiguration = builder.build()
        toolbar.setupWithNavController(navController, appBarConfiguration)

    }

    private fun fetchRemoteConfigAndUpdate() {
        sharedViewModel.getRemoteConfig(Constants.REMOTECONFIGID)

        Log.d("LogCheck", "fetchRemoteConfigAndUpdate")
        lifecycleScope.launchWhenStarted {
            sharedViewModel.remoteConfig.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data.let { remoteConfig ->
                            if (remoteConfig.versionName != Constants.VERSION_NAME) {
                                isUpdateBannerShown = true
                                showUpdateBottomSheet(result.data)
                            } else {
                                if (!isUpdateBannerShown) {
                                    if (!isAppInstalled(remoteConfig.banner.packageName)) {
                                        isUpdateBannerShown = true
                                        showBannerBottomSheet(remoteConfig.banner)
                                    }
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        Log.e("MainActivityLOG", "Error fetching remote config: ${result.message}")
                    }

                    else -> {}
                }
            }
        }
    }

    private fun isAppInstalled(appPackageInfo: String): Boolean {
        return try {
            packageManager.getPackageInfo(appPackageInfo, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
            && view is EditText && !view.javaClass.name.startsWith("android.webkit.")
        ) {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            val hideKeyboard = !rect.contains(ev.rawX.toInt(), ev.rawY.toInt())
            if (hideKeyboard) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showUpdateBottomSheet(data: RemoteConfigModel) {

        val bottomSheetBinding = BottomSheetUpdateBinding.inflate(layoutInflater)

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.updateButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.hfad.ismlarmanosi2023")
            )
            startActivity(intent)
        }

        bottomSheetDialog.show()

        bottomSheetDialog.setOnCancelListener {
            if (!isAppInstalled(data.banner.packageName)) {
                showBannerBottomSheet(data.banner)
            }
        }
    }

    private fun showBannerBottomSheet(bannerModel: BannerModel) {
        Log.d("AppLogging", "showBannerBottomSheet")


        val bottomSheetBinding = BottomSheetBannerBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        Glide
            .with(bottomSheetBinding.ivBanner)
            .load(bannerModel.imageLink)
            .fitCenter()
            .into(bottomSheetBinding.ivBanner)

        bottomSheetBinding.downloadButton.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(bannerModel.playMarketLink)
            )
            startActivity(intent)
        }

        var shownTimes = getBannerShown()

        when {
            shownTimes == 0 -> {
                bottomSheetDialog.show()
                shownTimes++
                saveBannerShown(shownTimes)
                Log.d("AppLogging", "shownTimes $shownTimes")

            }

            shownTimes >= 2 -> {
                saveBannerShown(0)
            }

            else -> {
                shownTimes++
                saveBannerShown(shownTimes)
            }
        }
    }

    private fun saveBannerShown(times: Int) {
        Log.d("AppLogging", "saveBannerShown")

        sharedPreferences.edit().putInt(Constants.KEY_BANNER_SHOWN, times).apply()
    }

    private fun getBannerShown(): Int {
        Log.d("AppLogging", "getBannerShown")

        return sharedPreferences.getInt(Constants.KEY_BANNER_SHOWN, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AppLogging", "MainActivity is being destroyed")
    }
}