package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket



class MainActivity : AppCompatActivity() {

    lateinit var socket : Socket

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val matchingButton = findViewById<Button>(R.id.button_Matching)
        val leadButton = findViewById<Button>(R.id.button_Leaderboard)
        val myProfile = findViewById<ImageButton>(R.id.myprofile)

        val email = intent.getStringExtra("email")

        matchingButton.setOnClickListener{

            val matching = Intent(this, OmokActivity::class.java)
            startActivity(matching)

        }

        leadButton.setOnClickListener{

            val leader = Intent(this, LeaderBoard::class.java)
            startActivity(leader)

        }

        myProfile.setOnClickListener{

            val myinfo = Intent(this, MyInfo::class.java)
            myinfo.putExtra("email", email)
            startActivity(myinfo)

        }

    }

}