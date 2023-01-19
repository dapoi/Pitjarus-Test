package com.dapoi.pitjarustest.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dapoi.pitjarustest.databinding.ActivityLoginBinding
import com.dapoi.pitjarustest.presentation.viewmodel.ShopViewModel
import com.dapoi.pitjarustest.presentation.viewmodel.UserPreferenceViewModel
import com.dapoi.pitjarustest.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val userPreferenceViewModel: UserPreferenceViewModel by viewModels()
    private val shopViewModel: ShopViewModel by viewModels()
    private val binding: ActivityLoginBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        userPreferenceViewModel.hasLogin.observe(this) { hasLogin ->
            if (hasLogin) {
                startActivity(Intent(this, ShopActivity::class.java))
                finish()
            }
        }

        binding.apply {
            btnLogin.setOnClickListener {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()

                when {
                    username.isBlank() -> {
                        tilUsername.error = "Username is required"
                    }
                    password.isBlank() -> {
                        tilPassword.error = "Password is required"
                    }
                    else -> {
                        tilUsername.error = null
                        tilPassword.error = null
                        userLogin(username, password)
                    }
                }
            }
        }
    }

    private fun userLogin(username: String, password: String) {
        shopViewModel.login(username, password).observe(this) { response ->
            when (response) {
                is Resource.Loading -> {
                    showControl(true)
                }
                is Resource.Success -> {
                    showControl(false)
                    saveUserData(username)
                    response.data?.let {
                        shopViewModel.insertShop(it.stores)
                    }
                    Intent(this, ShopActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                is Resource.Error -> {
                    showControl(false)
                    AlertDialog.Builder(this)
                        .setTitle("Login Failed")
                        .setMessage(response.message)
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }

    private fun saveUserData(username: String) {
        lifecycleScope.launch {
            userPreferenceViewModel.apply {
                saveHasLogin(true)
                saveUsername(username)
            }
        }
    }

    private fun showControl(state: Boolean) {
        binding.apply {
            if (state) {
                progressBar.visibility = View.VISIBLE
                btnLoginText.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                btnLoginText.visibility = View.VISIBLE
            }
        }
    }
}