package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.retrogamingstore.R
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.databinding.DialogChangePasswordBinding
import com.example.retrogamingstore.databinding.FragmentProfileBinding
import com.example.retrogamingstore.model.User
import com.example.retrogamingstore.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        userRepository = UserRepository(database.userDao(), requireContext())

        setupLogoutButton()
        setupOrderHistoryButton()
        setupChangePasswordButton()
        loadUserData()
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                currentUser = withContext(Dispatchers.IO) {
                    userRepository.getCurrentUser()
                }

                currentUser?.let { user ->
                    binding.textUsername.text = user.username
                    binding.textEmail.text = user.email
                    binding.textRole.text = when (user.role) {
                        1 -> "Клієнт"
                        2 -> "Адміністратор"
                        else -> "Невідома роль"
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Помилка завантаження даних: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupChangePasswordButton() {
        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Зміна пароля")
            .setView(dialogBinding.root)
            .setPositiveButton("Зберегти", null)
            .setNegativeButton("Скасувати", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val currentPassword = dialogBinding.editCurrentPassword.text.toString()
                val newPassword = dialogBinding.editNewPassword.text.toString()
                val confirmPassword = dialogBinding.editConfirmPassword.text.toString()

                when {
                    currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                        Toast.makeText(requireContext(), "Заповніть всі поля", Toast.LENGTH_SHORT).show()
                    }
                    newPassword != confirmPassword -> {
                        Toast.makeText(requireContext(), "Новий пароль та підтвердження не співпадають", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        changePassword(currentPassword, newPassword, dialog)
                    }
                }
            }
        }

        dialog.show()
    }

    private fun changePassword(currentPassword: String, newPassword: String, dialog: AlertDialog) {
        lifecycleScope.launch {
            try {
                val userId = currentUser?.id ?: return@launch
                val username = currentUser?.username ?: return@launch

                val isPasswordCorrect = withContext(Dispatchers.IO) {
                    val user = userRepository.getUserByUsernameAndPassword(username, currentPassword)
                    user != null
                }

                if (isPasswordCorrect) {
                    withContext(Dispatchers.IO) {
                        userRepository.updateUserPassword(userId, newPassword)
                    }
                    Toast.makeText(requireContext(), "Пароль успішно змінено", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                    // Оновлюємо поточного користувача
                    currentUser = withContext(Dispatchers.IO) {
                        userRepository.getUserById(userId)
                    }
                    currentUser?.let { userRepository.saveCurrentUser(it) }
                } else {
                    Toast.makeText(requireContext(), "Неправильний поточний пароль", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Помилка зміни пароля: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            // Очищаємо дані поточного користувача при виході
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

    private fun setupOrderHistoryButton() {
        binding.btnOrderHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_orderHistoryFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}