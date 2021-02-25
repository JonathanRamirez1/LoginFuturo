package com.jonathan.loginfuturo.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.jonathan.loginfuturo.R
import com.jonathan.loginfuturo.isValidEmail
import com.jonathan.loginfuturo.validate
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    private val firebaseAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        editTextEmail.validate {
            if (isValidEmail(it)) {
                editTextEmail.error = null
            } else {
                editTextEmail.error = getString(R.string.login_forgot_password)
            }
        }

        buttonGoLogInForgot.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        buttonForgotPassword.setOnClickListener {
            val email = editTextEmail.text.toString()
            if (isValidEmail(email)) {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
                    Toast.makeText(this, "Email has been sent to reset your password", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            } else {
                Toast.makeText(this, "Please make sure the email is correct", Toast.LENGTH_SHORT).show()
            }
        }
    }
}