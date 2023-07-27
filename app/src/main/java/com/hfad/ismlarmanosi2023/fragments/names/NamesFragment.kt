package com.hfad.ismlarmanosi2023.fragments.names

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.ismlarmanosi2023.R
import com.hfad.ismlarmanosi2023.data.NamesViewModel
import com.hfad.ismlarmanosi2023.databinding.FragmentNamesBinding
import com.hfad.ismlarmanosi2023.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NamesFragment : Fragment() {
    private var _binding: FragmentNamesBinding? = null
    private val binding get() = _binding!!

    private val adapter: NamesAdapter by lazy { NamesAdapter() }
    private val nNamesViewModel: NamesViewModel by viewModels()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNamesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard(requireActivity())
            }
            false
        }

        showRecyclerView()
        searchList()

        lifecycleScope.launch {
            nNamesViewModel.getAllData.collect { data ->
                adapter.setData(data)
            }
        }


        return (binding.root)
    }

    private fun showRecyclerView() {
        val recyclerView = binding.recyclerviewList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.itemAnimator = LandingAnimator().apply {
            addDuration = 200
        }
    }

    private fun searchList() {
        val searchView = binding.search

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    performSearch(query)
                }
                return true
            }


        })

    }

    private fun performSearch(query: String) {
        var searchQuery: String = query
        searchQuery = "%$searchQuery%"

        lifecycleScope.launch {
            nNamesViewModel.searchName(searchQuery).collect { list ->
                list?.let {
                    if (it.isEmpty()) {
                        showEmptyDatabaseView(true)
                    } else {
                        showEmptyDatabaseView(false)
                        val sortedList = it.sortedBy { namesData -> namesData.name }
                        adapter.setData(sortedList)
                    }
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.names_manu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_liked -> findNavController().navigate(R.id.action_namesFragment_to_likedFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showEmptyDatabaseView(emptyDatabase: Boolean) {
        if (emptyDatabase) {
            binding.loNames.visibility = View.INVISIBLE
            binding.noNamesLo.visibility = View.VISIBLE
        } else {
            binding.loNames.visibility = View.VISIBLE
            binding.noNamesLo.visibility = View.INVISIBLE
        }
    }

}