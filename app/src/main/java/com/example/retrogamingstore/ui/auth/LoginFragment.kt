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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        usernameEditText = view.findViewById(R.id.edittext_username)
        passwordEditText = view.findViewById(R.id.edittext_password)
        loginButton = view.findViewById(R.id.button_login)
        registerButton = view.findViewById(R.id.button_register)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(username, password)
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        return view
    }

    private fun loginUser(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Заповніть всі поля", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val database = AppDatabase.getInstance(requireContext())
                val user = withContext(Dispatchers.IO) {
                    database.userDao().findUser(username, password)
                }

                if (user != null) {
                    when (user.role) {
                        1 -> findNavController().navigate(R.id.homeFragment)  // Клієнт
                        2 -> findNavController().navigate(R.id.adminFragment) // Адміністратор
                        else -> Toast.makeText(requireContext(), "Невідома роль", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Невірний логін або пароль", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Помилка авторизації: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
