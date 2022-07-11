package com.example.test

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var count = 5

        val matchingButton = findViewById<ImageButton>(R.id.button_Matching)
        val leadButton = findViewById<ImageButton>(R.id.button_Leaderboard)
        val myProfile = findViewById<ImageButton>(R.id.myprofile)
        val easterEgg = findViewById<ImageButton>(R.id.easterEgg)

        val email = intent.getStringExtra("email")

        matchingButton.setOnClickListener{

            val matching = Intent(this, OmokActivity::class.java)
            matching.putExtra("email", email)
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

        easterEgg.setOnClickListener{

            count --

            if(count == 0){

                val easteregg = Intent(Intent.ACTION_VIEW, Uri.parse("https://koreaomok.modoo.at/"))
                startActivity(easteregg)

            }

        }

    }

}
