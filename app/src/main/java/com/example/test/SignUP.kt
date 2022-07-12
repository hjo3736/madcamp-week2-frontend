package com.example.test

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.*

class SignUP : AppCompatActivity() {
    lateinit var queue : RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val button = findViewById<Button>(R.id.button_sign_up)
        val textEmail = findViewById<EditText>(R.id.editTextEmail)
        val textNickname = findViewById<EditText>(R.id.editTextNickname)
        val schoolSpinner = findViewById<Spinner>(R.id.schoolSpinner)

        textEmail.setText(intent.getStringExtra("key"))

        val schoolNames = resources.getStringArray(R.array.school_array).map { s -> School(s) }.toList()
        val schoolAdapter = SchoolAdapter(this, schoolNames)
        schoolSpinner.adapter = schoolAdapter

        button.setOnClickListener {
            val email = textEmail.text.toString()
            val nickname = textNickname.text.toString()
            val school = (schoolSpinner.selectedItem as School).shortName

            if (nickname == "") {
                textNickname.setHintTextColor(Color.RED)
            }
            else {
                queue = Volley.newRequestQueue(this)

                val log = IP.getIP() + "/user/signup/email/" + email + "/nickname/" + nickname + "/school/" + school

                val stringRequest = StringRequest(
                    Request.Method.POST,
                    log,
                    { response ->
                        val main = Intent(this, MainActivity::class.java)
                        main.putExtra("email", email)
                        startActivity(main)
                        finish()
                    },
                    { error ->
                        Log.d("bad", error.message.toString())
                    })

                stringRequest.setShouldCache(false)
                queue.add(stringRequest)
            }
        }
    }
}
