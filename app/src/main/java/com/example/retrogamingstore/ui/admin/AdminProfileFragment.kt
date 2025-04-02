package com.example.retrogamingstore.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.retrogamingstore.R
import com.example.retrogamingstore.databinding.FragmentAdminProfileBinding
import androidx.navigation.NavOptions
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.repository.UserRepository
import kotlinx.coroutines.launch

class AdminProfileFragment : Fragment() {

    private lateinit var userRepository: UserRepository
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
        val database = AppDatabase.getDatabase(requireContext())
        userRepository = UserRepository(database.userDao(), requireContext())

        setupLogoutButton()
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                userRepository.clearCurrentUser()

                findNavController().navigate(R.id.action_global_to_loginFragment)

                requireActivity().run {
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    // Метод для очищення всіх даних користувача
    private fun clearUserData() {
        // Тут можна додати очищення будь-яких даних користувача
        // Наприклад, кеш, тимчасові файли, SharedPreferences, тощо
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}