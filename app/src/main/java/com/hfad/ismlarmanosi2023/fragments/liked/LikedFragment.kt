package com.hfad.ismlarmanosi2023.fragments.liked

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.hfad.ismlarmanosi2023.R
import com.hfad.ismlarmanosi2023.data.NamesViewModel
import com.hfad.ismlarmanosi2023.dataLiked.LikedData
import com.hfad.ismlarmanosi2023.dataLiked.LikedViewModel
import com.hfad.ismlarmanosi2023.databinding.FragmentLikedBinding
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.ScaleInAnimator
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LikedFragment : Fragment() {
    private var _binding: FragmentLikedBinding? = null
    private val binding get() = _binding!!

    private val adapter: LikedAdapter by lazy { LikedAdapter() }
    private val mLikedViewModel: LikedViewModel by viewModels()
    private val nNamesViewModel: NamesViewModel by viewModels()

    private val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(true)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLikedBinding.inflate(inflater, container, false)

        Glide.with(this)
            .load(R.drawable.family) // Assuming family is the name of your image resource
            .into(binding.familyIv)
        Glide.with(this)
            .load(R.drawable.ic_liked) // Assuming family is the name of your image resource
            .into(binding.icLikedIv)

        recyclerViewLiked()
        val adRequest = AdRequest.Builder().build()
        binding.adView6.loadAd(adRequest)
        binding.adView7.loadAd(adRequest)
        binding.adView8.loadAd(adRequest)
        mLikedViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            checkIfDatabaseEmpty(data)
            adapter.setData(data)

        }


        lifecycleScope.launch {
            nNamesViewModel.getAllData.collect { data ->
                adapter.setNamesData(data)
            }
        }



        emptyDatabase.observe(viewLifecycleOwner) {
            showEmptyDatabaseView(it)
        }

        return (binding.root)
    }


    private fun recyclerViewLiked() {
        val recyclerView = binding.recyclerviewLiked
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.itemAnimator = ScaleInAnimator().apply {
            addDuration = 200
        }
    }

    private fun checkIfDatabaseEmpty(likedData: List<LikedData>) {
        emptyDatabase.value = likedData.isEmpty()
    }

    private fun showEmptyDatabaseView(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            binding.noDataLo.visibility = View.VISIBLE
        } else {
            binding.noDataLo.visibility = View.INVISIBLE
        }
    }

}