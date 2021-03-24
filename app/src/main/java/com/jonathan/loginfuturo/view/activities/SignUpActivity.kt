package com.jonathan.loginfuturo.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jonathan.loginfuturo.*
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    private val firebaseAuth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        buttonGoLogIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        buttonSignUp.setOnClickListener {
            val email = editTextEmailSignUp.text.toString()
            val password = editTextPasswordSignUp.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()
            if (isValidEmail(email) && isValidPassword(password) && isValidConfirmPassword(password, confirmPassword)) {
                signUpByEmail(email, password)
            } else {
                Toast.makeText(
                    this,
                    "Please make sure all data is correct",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //TODO INTENTAR PASAR AL MODEL y Explicaion en el archivo Extensions y los mensajes a una constant

        editTextEmailSignUp.validate {
            if (isValidEmail(it)) {
                editTextEmailSignUp.error = null
            } else {
                editTextEmailSignUp.error = getString(R.string.sign_up_email_is_no_valid)
            }
        }

        editTextPasswordSignUp.validate {
            if (isValidPassword(it)) {
                editTextPasswordSignUp.error = null
            } else {
                editTextPasswordSignUp.error = getString(R.string.sign_up_password_is_no_valid)
            }
        }

        editTextConfirmPassword.validate {
            if (isValidConfirmPassword((editTextPasswordSignUp.text.toString()), it)) {
                editTextConfirmPassword.error = null
            } else {
                editTextConfirmPassword.error = getString(R.string.sign_up_confirm_password_is_no_valid)
            }
        }
    }

    /** Creacion de Usuario usando email y password **/

    private fun signUpByEmail(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this) {
                            Toast.makeText(this, "Email has been sent to you. Please confirm before sign in.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                    } else {
                        Toast.makeText(this, "Unexpected error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}
/* /** Validacion de los compos que no sean nulo o esten en blanco **/

    private fun isValidEmailAndPassword(email: String, password: String) : Boolean {
        return email.isNotEmpty() &&
                password.isNotEmpty() &&
                password == editTextConfirmPassword.text.toString()
    }*/

