package com.hfad.ismlarmanosi2023.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hfad.ismlarmanosi2023.R
import com.hfad.ismlarmanosi2023.databinding.FragmentMenuBinding
import com.hfad.ismlarmanosi2023.language.MyPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    lateinit var myPreference: MyPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        myPreference = MyPreference(requireContext())


        binding.llLikedOnes.setOnClickListener {
            findNavController().navigate(R.id.action_namesFragment_to_likedFragment)
        }

        binding.llNotFound.setOnClickListener {

            openTelegramChat()
        }
        binding.llRate.setOnClickListener {
            otherApps()
        }

        binding.llOthers.setOnClickListener {
            otherApps()
        }

        binding.llShare.setOnClickListener {
            share()
        }

        var lang: String? = myPreference.getLoginCount()
        when (lang) {
            "en" -> binding.rbLotincha.isChecked = true
            "uz" -> binding.rbKirilcha.isChecked = true
            else -> binding.rbLotincha.isChecked = true
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            lang = when (checkedId) {
                R.id.rb_lotincha -> "en"
                R.id.rb_kirilcha -> "uz"
                else -> "en"
            }
            // Save the selected language in SharedPreferences
            myPreference.setLoginCount(lang!!)
            activity?.recreate()
        }

        return (binding.root)
    }

    private fun openTelegramChat() {
        try {
            val telegramUri = Uri.parse("tg://resolve?domain=ismlarmanosidasturi")
            val intent = Intent(Intent.ACTION_VIEW, telegramUri)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case when Telegram is not installed on the device
            // You can show an error message or prompt the user to install Telegram
        }
    }

    private fun otherApps() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/developer?id=AppLab+INC")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun share() {
        val shareText =
            "Ismlar Ma'nosi va Boshqa foydali ilovalarni quyidagi havola orqali yuklab olin va sinab ko'rin: \n https://play.google.com/store/apps/developer?id=AppLab+INC"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}