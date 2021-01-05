package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.myapplication.MainActivity as MainActivity

class Register : AppCompatActivity() {

    lateinit var userEmail:EditText
    lateinit var userPassword:EditText
    lateinit var signUpButton: Button
    lateinit var alreadyHaveAnAccountButton: Button
    private lateinit var mAuth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.getReference("Users")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()


        userEmail = findViewById<EditText>(R.id.emailTextField)
        userPassword = findViewById<EditText>(R.id.passwordTextField)
        signUpButton = findViewById<Button>(R.id.signUpButton)
        alreadyHaveAnAccountButton = findViewById<Button>(R.id.alreadyHaveAnAccountButton)

            signUpButton.setOnClickListener {

                val email = userEmail.text.toString().trim()
                val password = userPassword.text.toString().trim()
                //Validate user info
                val valid = validateUserInfo(email, password)

                //if valid, Register user
                if (valid) {
                    registerUser(email,password)
                }
            }

        alreadyHaveAnAccountButton.setOnClickListener {
            //val intent = Intent(this, Login::class.java)
            //startActivity(intent)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(email:String, password:String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    uploadUserDataToDatabase(email)
                    // Sign in success, update UI with the signed-in user's information
                    Log.w("SIGN IN - Successful",
                        "createUserWithEmail:failure",
                        task.exception)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    uploadUserDataToDatabase(email)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SIGN UP -  Not Successful",
                        "createUserWithEmail:failure",
                        task.exception)
                    Toast.makeText(baseContext,
                        "Authentication failed. Email already exists please try Signing In. ",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadUserDataToDatabase(email: String) {

        var uid = mAuth.currentUser?.uid
        var user = User(email)

        myRef.child(uid!!).push().setValue(email)


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

        if(password.length < 6) {
            userPassword.error = "Password Must be >= 6 Characters"
            return false
        }
         return true
    }

}