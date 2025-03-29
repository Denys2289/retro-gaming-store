package com.example.retrogamingstore.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.retrogamingstore.R
import com.example.retrogamingstore.database.AppDatabase
import com.example.retrogamingstore.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var avatarEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        usernameEditText = view.findViewById(R.id.edittext_username)
        emailEditText = view.findViewById(R.id.edittext_email)
        passwordEditText = view.findViewById(R.id.edittext_password)
        confirmPasswordEditText = view.findViewById(R.id.edittext_confirm_password)
        registerButton = view.findViewById(R.id.button_register)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Заповніть всі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Паролі не співпадають", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(username, email, password)
        }

        return view
    }

    private fun registerUser(username: String, email: String, password: String) {
        lifecycleScope.launch {
            val database = AppDatabase.getInstance(requireContext())
            val userDao = database.userDao()

            val existingUser = withContext(Dispatchers.IO) {
                userDao.getUserByUsernameOrEmail(username, email)
            }

            if (existingUser != null) {
                Toast.makeText(requireContext(), "Користувач із таким логіном або email вже існує", Toast.LENGTH_SHORT).show()
                return@launch
            }

            withContext(Dispatchers.IO) {
                val newUser = User(username = username, email = email, password = password)
                userDao.insertUser(newUser)
            }

            Toast.makeText(requireContext(), "Реєстрація успішна", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }
}
