package com.example.adminuser

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminuser.models.Users
import com.example.adminuser.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase


class SignInActivity : AppCompatActivity() {

    private var binding: ActivitySignInBinding? = null
    private var progressDialog: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        progressDialog = ProgressDialog(this@SignInActivity)
        progressDialog!!.setTitle("Login")
        progressDialog!!.setMessage("Please Wait \n Validation in Progress")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("319310297783-pi682lf0pgvgr28rcv45dlr6pima6oa2.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        binding!!.btnSignIn.setOnClickListener {
            if (binding!!.txtEmail.text.toString()
                    .isNotEmpty() && binding!!.txtPassword.text.toString()
                    .isNotEmpty()
            ) {
                progressDialog!!.show()
                mAuth!!.signInWithEmailAndPassword(
                    binding!!.txtEmail.text.toString(),
                    binding!!.txtPassword.text.toString()
                )
                    .addOnCompleteListener { task ->
                        progressDialog!!.dismiss()
                        if (task.isSuccessful) {
                            val intent = Intent(this@SignInActivity, MapsActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@SignInActivity,
                                task.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this@SignInActivity, "Enter Credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        if (mAuth!!.currentUser != null) {
            val intent = Intent(this@SignInActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        binding!!.txtClickSignUp.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding!!.btnGoogle.setOnClickListener { signIn() }
    }

    private var RC_SIGN_IN = 9001

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                Log.d("SignIn", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {

                // Google Sign In failed
                Log.w("SignIn", "Google Sign In failed.", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Log.w("SignIn", "signInWithCredential:success")
                    val user = mAuth!!.currentUser
                    val users = Users()
                    users.userId = user!!.uid
                    users.userName = user.displayName
                    users.profilePic = user.photoUrl.toString()
                    firebaseDatabase!!.reference.child("Users").child(user.uid).setValue(users)
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@SignInActivity, "Sign in with Google", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@SignInActivity, "Sorry error auth", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}