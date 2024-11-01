package com.example.universe.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.universe.MainActivity
import com.example.universe.R
import com.example.universe.databinding.ActivitySignUpBinding
import com.example.universe.model.User
import com.example.universe.utils.USER_NODE
import com.example.universe.utils.USER_PROFILE_FOLDER
import com.example.universe.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var user: User
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){      // this will get the image from user's device
        uri ->

        uri?.let {

           uploadImage(uri, USER_PROFILE_FOLDER){
                if (it == null){
                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                }else{
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        user = User()

        // if we open this activity from Edit Profile

        if (intent.hasExtra("MODE")){
            if (intent.getIntExtra("MODE",-1) == 1){
                binding.linearLayoutSignUp.visibility = View.GONE
                binding.profileImage.visibility = View.VISIBLE
                "Update Profile".also { binding.signUpText.text = it }
                "Update Your \nAccount".also { binding.createAccountTVDummy?.text = it }
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {

                        user= it.toObject(User::class.java)!!

                        binding.nameEditText.editText?.setText(user.name)
                        binding.emailEditText.editText?.setText(user.email)
                        binding.passwordEditText.editText?.setText(user.password)

                        if (!user.image.isNullOrEmpty()) {

                            // load image using picasso
                            Picasso.get().load(user.image).into(binding.profileImage)
                        }

                    }
            }
        }

        // if we use normal signUp
        else {
            binding.linearLayoutSignUp.visibility = View.VISIBLE
            binding.profileImage.visibility = View.GONE
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.LoginTV.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signUpBtn.setOnClickListener {

            // if we are updating data from edit profile

            if (intent.hasExtra("MODE")){
                if (intent.getIntExtra("MODE",-1) == 1){

                    Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).set(user).addOnSuccessListener {
                        Toast.makeText(this,"Profile Updated",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity,MainActivity::class.java))
                        finish()
                    }
                }
            }

            // else if user is opening app first time he will see a normal signUp Screen

            else{


            if (binding.nameEditText.editText?.text.toString()
                    .isEmpty() or binding.emailEditText.editText?.text.toString()
                    .isEmpty() or binding.passwordEditText.editText?.text.toString().isEmpty()
            ) {

                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()

            }

            // check if user is logging in from official university email

            else if (!binding.emailEditText.editText?.text.toString().contains("iqra.edu.pk")) {

                Toast.makeText(
                    this@SignUpActivity,
                    "Please use your IQRA email",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // if no issues then register the user

            else {

                Firebase.auth.createUserWithEmailAndPassword(
                    binding.emailEditText.editText?.text.toString(),
                    binding.passwordEditText.editText?.text.toString()
                ).addOnCompleteListener { result ->

                    if (result.isSuccessful) {

                        user.name =
                            binding.nameEditText.editText?.text.toString()      //set user name to the name entered
                        user.email =
                            binding.emailEditText.editText?.text.toString()    //set user email to the email entered
                        user.password =
                            binding.passwordEditText.editText?.text.toString()  //set user password to the password entered

                       Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).set(user).addOnSuccessListener {
                           Toast.makeText(this@SignUpActivity,"Success",Toast.LENGTH_SHORT).show()
                       }


                        Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
            }
        }

        binding.materialCardView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.profileImage.setOnClickListener{
            galleryLauncher.launch("image/*")
        }
    }
}