package com.example.test

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class LoginCheck : AppCompatActivity() {

    val PERMISSIONS_REQUEST_CODE = 100
    val GOOGLE_LOGIN = 1000
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var queue : RequestQueue
    val ip = "http://192.249.18.128"

    override fun onCreate(savedInstanceState: Bundle?) {

        val permissioncheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

        if(permissioncheck1 != PackageManager.PERMISSION_GRANTED){

            val permissions = arrayOf(Manifest.permission.INTERNET)

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)){

                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)

            }else{

                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)

            }

        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logincheck)

        queue = Volley.newRequestQueue(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        if(GoogleSignIn.getLastSignedInAccount(this) != null){

            queue = Volley.newRequestQueue(this)

            if (queue != null) {

                val log = ip + "/user/login/email/" + GoogleSignIn.getLastSignedInAccount(this)?.email.toString()

                val stringRequest = StringRequest(Request.Method.GET,
                    log,
                    { response ->
                        if (response == "YES") {
                            val main = Intent(this, MainActivity::class.java)
                            main.putExtra("email",GoogleSignIn.getLastSignedInAccount(this)?.email.toString())
                            startActivity(main)
                            finish()
                        }
                    },
                    { error ->
                        if (error.networkResponse.statusCode == 404) {
                            val signUp = Intent(this, SignUP::class.java)
                            signUp.putExtra("key", GoogleSignIn.getLastSignedInAccount(this)?.email.toString())
                            startActivity(signUp)
                            finish()
                        }
                    }
                )

                stringRequest.setShouldCache(false)
                queue.add(stringRequest)
            }

            val main = Intent(this, MainActivity::class.java)
            main.putExtra("email",GoogleSignIn.getLastSignedInAccount(this)?.email.toString())
            startActivity(main)
            finish()

        } else{

            googleLogin()

        }

    }

    fun googleLogin() {

        var signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GOOGLE_LOGIN -> {

                val task = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
                if (task!!.isSuccess) {

                    queue = Volley.newRequestQueue(this)

                    if (queue != null) {

                        val log = ip + "/user/login/email/" + task.signInAccount!!.email

                        val stringRequest = StringRequest(Request.Method.GET,
                            log,
                            { response ->
                                if(response == "YES"){

                                    val main = Intent(this, MainActivity::class.java)
                                    main.putExtra("email",task.signInAccount!!.email)
                                    startActivity(main)
                                    finish()

                                }
                            },
                            { error ->
                                if (error.networkResponse.statusCode == 404) {

                                    val signUp = Intent(this, SignUP::class.java)
                                    signUp.putExtra("key",task.signInAccount!!.email)
                                    startActivity(signUp)
                                    finish()


                                }
                            })

                        stringRequest.setShouldCache(false)
                        queue.add(stringRequest)

                    }

                }

            }

            else -> {

                System.exit(0)

            }

        }

    }

}