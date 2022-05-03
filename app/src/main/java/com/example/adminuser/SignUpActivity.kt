package com.example.adminuser

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminuser.Models.Users
import com.example.adminuser.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {
    private var binding: ActivitySignUpBinding? = null
    private var mAuth: FirebaseAuth? = null
    var database: FirebaseDatabase? = null
    var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = ProgressDialog(this@SignUpActivity)
        progressDialog!!.setTitle("Creating Account")
        progressDialog!!.setMessage("We're creating your account")

        binding!!.btnSignUp.setOnClickListener {
            if (!binding!!.txtUsername.text.toString()
                    .isEmpty() && !binding!!.txtEmail.text.toString().isEmpty()
                && !binding!!.txtPassword.text.toString().isEmpty()
            ) {
                progressDialog!!.show()
                mAuth!!.createUserWithEmailAndPassword(
                    binding!!.txtEmail.text.toString(),
                    binding!!.txtPassword.text.toString()
                )
                    .addOnCompleteListener { task ->
                        progressDialog!!.dismiss()
                        if (task.isSuccessful) {
                            val user = Users(
                                binding!!.txtUsername.text.toString(),
                                binding!!.txtEmail.text.toString(),
                                binding!!.txtPassword.text.toString()
                            )
                            val id = task.result.user?.uid
                            if (id != null) {
                                database!!.reference.child("Users").child(id).setValue(user)
                            }
                            Toast.makeText(
                                this@SignUpActivity,
                                "Sign Up Successful",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this@SignUpActivity, "Enter Credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding!!.txtAlreadyHaveAccount.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}