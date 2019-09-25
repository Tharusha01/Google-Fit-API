package com.saukya.wellness.googlefitlogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val back=findViewById<Button>(R.id.btn_Back)
        val register=findViewById<Button>(R.id.btn_Register)
        val userEmail=findViewById<EditText>(R.id.et_User_Email)
        val userPassword=findViewById<EditText>(R.id.et_User_Password)

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
                if(email == "email" && pass == "pass"){
                    Toast.makeText(this,"Successfully registered...!", Toast.LENGTH_SHORT).show()
                    //register()
                    val intent = Intent(this, DetailsActivity::class.java)
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
}
private operator fun Button.invoke() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
