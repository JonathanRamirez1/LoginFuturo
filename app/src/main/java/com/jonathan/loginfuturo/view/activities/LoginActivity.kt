package com.jonathan.loginfuturo.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*
import com.jonathan.loginfuturo.*
import com.jonathan.loginfuturo.R
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var currentUser: FirebaseUser

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val mGoogleApiClient : GoogleApiClient by lazy { getGoogleApiClient() }
    private val loginManager : LoginManager by lazy {  LoginManager.getInstance() }
    //CONSTANTS
    private val REQUEST_CODE_GOOGLE_SIGN_IN = 99
    private var callbackManager : CallbackManager? = null
    private val REQUEST_CODE_FACEBOOK_SIGN_IN = 98

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(applicationContext)
//        AppEventsLogger.activateApp(this)
        setContentView(R.layout.activity_login)

        buttonSignUpLogin.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        buttonLogInGoogle.setOnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(intent, REQUEST_CODE_GOOGLE_SIGN_IN)
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmailLogin.text.toString()
            val password = editTextPasswordLogin.text.toString()
            if (isValidEmail(email) && isValidPassword(password)) {
                logInByEmail(email, password)
            } else {
                Toast.makeText(
                    this,
                    "Please make sure all data is correct",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

            loginByFacebookAccountIntoFirebase()


        //TODO si no se quiere validar los campos en tiempo real descomentar en el "buttonLogin.setOnClickListener" y borrar lo de abajo


        editTextEmailLogin.validate {
            if (isValidEmail(it)) {
                editTextEmailLogin.error = null
            } else {
                editTextEmailLogin.error = getString(R.string.sign_up_email_is_no_valid)
            }
        }

        editTextPasswordLogin.validate {
            if (isValidPassword(it)) {
                editTextPasswordLogin.error = null
            } else {
                editTextPasswordLogin.error = getString(R.string.sign_up_password_is_no_valid)
            }
        }
    }

    private fun getGoogleApiClient() : GoogleApiClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()
    }

    /** Inicio de sesion con google **/
    private fun loginByGoogleAccountIntoFirebase(googleAccount : GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
            if (mGoogleApiClient.isConnected) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun logInByEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (firebaseAuth.currentUser!!.isEmailVerified) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, "User must confirm email first", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Sign In Failure. Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun handleFacebookAccessToken(token : AccessToken) {
        buttonLogInFacebook.setOnClickListener {
            val credential = FacebookAuthProvider.getCredential(token.token)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                       // setUPCurrentUser()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        //   task.result?.user?.email ?:  REQUEST_CODE_FACEBOOK_SIGN_IN
                    }
                }
        }
    }

    private fun loginByFacebookAccountIntoFirebase() {
        callbackManager = CallbackManager.Factory.create()
       // buttonLogInFacebook.setReadPermissions("email", "public_profile")
        loginManager.logInWithReadPermissions(this, listOf("email", "public_profile"))
        loginManager.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    result?.let {
                        handleFacebookAccessToken(result.accessToken)
                    }
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, "facebook:onCancel", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(this@LoginActivity, "facebook:onError", Toast.LENGTH_SHORT).show()
                }

            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)


        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                val account = result.signInAccount
                loginByGoogleAccountIntoFirebase(account!!)
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "Connection Failed!!", Toast.LENGTH_SHORT).show()
    }
}


