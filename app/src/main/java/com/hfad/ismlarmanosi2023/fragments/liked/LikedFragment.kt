package com.hfad.ismlarmanosi2023.fragments.liked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.ismlarmanosi2023.dataLiked.LikedData
import com.hfad.ismlarmanosi2023.dataLiked.LikedViewModel
import com.hfad.ismlarmanosi2023.databinding.FragmentLikedBinding
import jp.wasabeef.recyclerview.animators.ScaleInAnimator

class LikedFragment : Fragment() {
    private var _binding: FragmentLikedBinding? = null
    private val binding get() = _binding!!

    private val adapter: LikedAdapter by lazy { LikedAdapter() }
    private val mLikedViewModel: LikedViewModel by viewModels()

    private val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLikedBinding.inflate(inflater, container, false)
        recyclerViewLiked()
        mLikedViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            checkIfDatabaseEmpty(data)
            adapter.setData(data)

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