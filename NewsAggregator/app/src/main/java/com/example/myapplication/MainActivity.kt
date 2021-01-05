package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.math.sign

class MainActivity : AppCompatActivity() {

    //Variables
    lateinit var userEmail: EditText
    lateinit var userPassword: EditText
    lateinit var signInButton: Button
    lateinit var dontHaveAnAccountButton: Button
    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize Firebase Auth instance.
        mAuth = FirebaseAuth.getInstance()

        userEmail = findViewById<EditText>(R.id.emailTextField)
        userPassword = findViewById<EditText>(R.id.passwordTextField)
        signInButton = findViewById<Button>(R.id.signInButton)
        dontHaveAnAccountButton = findViewById<Button>(R.id.dontHaveAnAccountButton)

        signInButton.setOnClickListener {

            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            val valid = validateUserInfo(email, password)

            //if valid, Login user
            if (valid) {
                signInUser(email,password)
            }
        }

        dontHaveAnAccountButton.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun signInUser(email:String, password:String) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Log.w("SIGN IN -  Not Successful", "logUserInWithEmail:failure", task.exception)
                Toast.makeText(baseContext,
                    "Log in failed: \nEmail or password are incorrect." ,
                    Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateUserInfo(email:String, password:String): Boolean {

        if(TextUtils.isEmpty(email)) {
            userEmail.error = "Email is Required."
            return false
        }
        if(TextUtils.isEmpty(password)) {
            userPassword.error = "Password is Required."
            return false
        }
        return true
    }



    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, Dashboard::class.java)
            Log.e("Action", "user signed in")
            startActivity(intent)
        }
    }
}


