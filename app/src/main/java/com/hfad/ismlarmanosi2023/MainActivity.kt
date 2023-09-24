package com.hfad.ismlarmanosi2023

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.hfad.ismlarmanosi2023.databinding.ActivityMainBinding
import com.hfad.ismlarmanosi2023.language.MyContextWrapper
import com.hfad.ismlarmanosi2023.language.MyPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var myPreference: MyPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("AppLogging", "App started")


        setupToolbarAndNavigation()
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AppLogging", "MainActivity is being destroyed")
    }

}