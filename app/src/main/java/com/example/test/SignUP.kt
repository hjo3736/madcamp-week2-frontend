package com.example.test

import android.content.ContextParams
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class SignUP : AppCompatActivity() {

    lateinit var queue : RequestQueue
    val ip = "http://172.10.5.121"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val button = findViewById<Button>(R.id.button_sign_up)
        val textEmail = findViewById<EditText>(R.id.editTextEmail)
        val textNickname = findViewById<EditText>(R.id.editTextNickname)
        val textSchool = findViewById<EditText>(R.id.editTextSchool)

        textEmail.setText(intent.getStringExtra("key"))

        button.setOnClickListener{

            val email = textEmail.text.toString()
            val nickname = textNickname.text.toString()
            val school = textSchool.text.toString()

            if(nickname == "" && school == ""){

                textNickname.setHintTextColor(Color.RED)
                textSchool.setHintTextColor(Color.RED)

            }else if(nickname == "" && school != ""){

                textNickname.setHintTextColor(Color.RED)

            }else if(school == "" && nickname != ""){

                textSchool.setHintTextColor(Color.RED)

            }else{

                queue = Volley.newRequestQueue(this)

                val log = ip + "/user/signup/email/" + email + "/nickname/" + nickname + "/school/" + school

                val stringRequest = StringRequest(Request.Method.POST,
                log,
                Response.Listener<String>{ response ->

                    val main = Intent(this, MainActivity::class.java)
                    main.putExtra("email", email)
                    startActivity(main)
                    finish()

                },
                Response.ErrorListener{ error ->
                    Log.d("bad", error.message.toString())
                })

                stringRequest.setShouldCache(false)
                queue.add(stringRequest)

            }

        }

    }
}