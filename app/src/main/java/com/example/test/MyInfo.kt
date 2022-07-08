package com.example.test

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MyInfo : AppCompatActivity() {

    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var queue : RequestQueue
    val ip = "http://172.10.5.121"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_info)

        val memail = findViewById<TextView>(R.id.memailinfo)
        val mnickname = findViewById<TextView>(R.id.mnicknameinfo)
        val mschool = findViewById<TextView>(R.id.mschoolinfo)
        val melo = findViewById<TextView>(R.id.meloinfo)
        val signOutButton = findViewById<Button>(R.id.button_sign_out)
        val quitButton = findViewById<Button>(R.id.button_quit)

        val email = intent.getStringExtra("email")

        memail.setText(email)

        queue = Volley.newRequestQueue(this)

        if(queue != null){

            val log = ip + "/user/email/" + email

            val jsonArraytRequest = JsonArrayRequest(Request.Method.GET,
                log,null,
                Response.Listener{response ->
                    mnickname.setText(response.getJSONObject(0).getString("nickname"))
                    mschool.setText(response.getJSONObject(0).getString("school"))
                    melo.setText(response.getJSONObject(0).getString("elo_rating"))
                },
                Response.ErrorListener { error ->

                }
            )

            jsonArraytRequest.setShouldCache(false)
            queue.add(jsonArraytRequest)

        }

        signOutButton.setOnClickListener{

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.requestsignout)

            val buttonyes = dialog.findViewById<ImageButton>(R.id.buttonyes)
            val buttonno = dialog.findViewById<ImageButton>(R.id.buttonno)

            buttonyes.setOnClickListener{

                val start = Intent(this, Splash::class.java)
                startActivity(start)
                finish()

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
                mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
                mGoogleSignInClient.signOut()

            }

            buttonno.setOnClickListener{

                dialog.dismiss()

            }

            dialog.show()

        }

        quitButton.setOnClickListener{

            val dialog = Dialog(this)
            dialog.setContentView(R.layout.requestquit)

            val buttonyes = dialog.findViewById<ImageButton>(R.id.buttonyes1)
            val buttonno = dialog.findViewById<ImageButton>(R.id.buttonno1)

            buttonyes.setOnClickListener {

                queue = Volley.newRequestQueue(this)

                if (queue != null) {

                    val log = ip + "/user/delete/email/" + email

                    val stringRequest = StringRequest(Request.Method.DELETE,
                        log,
                        Response.Listener { response ->

                            val start = Intent(this, Splash::class.java)
                            startActivity(start)
                            finish()

                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail().build()
                            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                            mGoogleSignInClient.signOut()

                        },
                        Response.ErrorListener { error ->
                            Log.d("why?", error.message.toString())
                        })

                    stringRequest.setShouldCache(false)
                    queue.add(stringRequest)
                }

            }

            buttonno.setOnClickListener{

                dialog.dismiss()

            }

            dialog.show()

        }

    }

}