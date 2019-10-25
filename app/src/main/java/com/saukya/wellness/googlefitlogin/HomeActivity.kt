package com.saukya.wellness.googlefitlogin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val back=findViewById<Button>(R.id.btn_Back)
        val register=findViewById<Button>(R.id.btn_Register)
        val userEmail=findViewById<EditText>(R.id.et_User_Email)
        val userPassword=findViewById<EditText>(R.id.et_User_Password)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION)
        }

        back.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        register.setOnClickListener{
            val email: String = userEmail.text.toString()
            val pass: String = userPassword.text.toString()
            print("userEmail "+ email +
                    "\n userPassword "+ pass)
            if (email.isNotEmpty() && pass.isNotEmpty()){
                if(email == "user" && pass == "test123#"){
                    Toast.makeText(this,"Successfully registered...!", Toast.LENGTH_SHORT).show()
                    //register()
                    val intent = Intent(this, TestActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Enter valied details", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText(this,"Successfully registered...!", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this,"Enter Email and password", Toast.LENGTH_SHORT).show()
            }

        }

    }
    companion object {
        private val REQUEST_LOCATION = 155
        private val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 150
    }
}
