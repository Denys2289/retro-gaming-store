package com.example.retrogamingstore.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentAdminProfileBinding

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLogoutButton()
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_global_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}