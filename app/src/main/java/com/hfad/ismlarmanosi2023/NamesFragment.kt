package com.hfad.ismlarmanosi2023

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hfad.ismlarmanosi2023.hideKeyboard
import com.hfad.ismlarmanosi2023.databinding.FragmentNamesBinding

class NamesFragment : Fragment() {
    private var _binding: FragmentNamesBinding? = null
    private val binding get() = _binding!!

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


        return (binding.root)
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

}