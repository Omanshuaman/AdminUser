package com.example.adminuser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.adminuser.Models.Upload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    //widgets
    private var uploadBtn: Button? = null
    private var showAllBtn: Button? = null
    private var imageView: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var mEditTextFileName: EditText? = null
    private var placePicker: Button? = null
    private var langlat: TextView? = null
    private var PLACE_PICKER_REQUEST =1

    val userid = FirebaseAuth.getInstance().currentUser!!.uid

    //vars
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val storageReference =
        FirebaseStorage.getInstance().reference.child("Photos").child(userid)
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        uploadBtn = findViewById(R.id.upload_btn)
        showAllBtn = findViewById(R.id.showall_btn)
        progressBar = findViewById(R.id.progressBar)
        imageView = findViewById(R.id.imageView)
        mEditTextFileName = findViewById(R.id.Name)

        progressBar!!.visibility = View.INVISIBLE
        mAuth = FirebaseAuth.getInstance()

        imageView!!.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        }

        uploadBtn!!.setOnClickListener {
            if (imageUri != null) {
                uploadToFirebase(imageUri!!)
            } else {
                Toast.makeText(this@MainActivity, "Please Select Image", Toast.LENGTH_SHORT).show()
            }
        }
        showAllBtn!!.setOnClickListener{
            openImagesActivity()
        }
    }

    private fun openImagesActivity() {
        val intent = Intent(this, ImagesActivity::class.java)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            imageView!!.setImageURI(imageUri)
        }
    }

    private fun uploadToFirebase(uri: Uri) {
        val fileRef =
            storageReference.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(uri)
            )
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri1: Uri ->
                val model = Upload(mEditTextFileName!!.text.toString(), uri1.toString())
                val modelId = databaseReference.push().key

                databaseReference.child("Image").child(userid).child(modelId!!)
                    .setValue(model)
                databaseReference.child("Just Photos").child(modelId).setValue(model)

                progressBar!!.visibility = View.INVISIBLE
                Toast.makeText(this@MainActivity, "Uploaded Successfully", Toast.LENGTH_SHORT)
                    .show()
            }
        }.addOnProgressListener { progressBar!!.visibility = View.VISIBLE }.addOnFailureListener {
            progressBar!!.visibility = View.INVISIBLE
            Toast.makeText(this@MainActivity, "Uploading Failed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(mUri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> Toast.makeText(this, "Setting is clicked", Toast.LENGTH_SHORT).show()
            R.id.groupChat -> Toast.makeText(this, "Group Chat is Started", Toast.LENGTH_SHORT)
                .show()
            R.id.logout -> {
                mAuth!!.signOut()
                val intent = Intent(this@MainActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
