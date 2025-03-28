package com.example.retrogamingstore.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
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
    private lateinit var clientRadioButton: RadioButton
    private lateinit var adminRadioButton: RadioButton
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
        clientRadioButton = view.findViewById(R.id.radio_client)
        adminRadioButton = view.findViewById(R.id.radio_admin)
        registerButton = view.findViewById(R.id.button_register)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val role = if (clientRadioButton.isChecked) 1 else 2 // 1 - клієнт, 2 - адміністратор

            registerUser(username, email, password, role)
        }

        return view
    }

    private fun registerUser(username: String, email: String, password: String, role: Int) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Заповніть всі поля", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val database = AppDatabase.getInstance(requireContext())
                val user = User(
                    username = username,
                    email = email,
                    password = password,
                    role = role
                )
                database.userDao().insertUser(user)
            }

            Toast.makeText(requireContext(), "Реєстрація успішна", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }
}
