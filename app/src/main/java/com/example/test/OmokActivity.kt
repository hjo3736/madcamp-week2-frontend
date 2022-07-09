package com.example.test

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONTokener
import org.w3c.dom.Text

class OmokActivity : AppCompatActivity(), MessageListener {
    lateinit var omokBoardView: ImageView
    lateinit var concedeButton: ImageButton
    private val serverUrl = "ws://192.249.18.128/websockets"
    private lateinit var myNickname: String
    private lateinit var mySchool: String
    private var myRating: Int = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_omok)

        val myNicknameTextView = findViewById<TextView>(R.id.myNicknameTextView)
        val myRatingTextView = findViewById<TextView>(R.id.myRatingTextView)

//        val email = intent.getStringExtra("email")
        val email = "leejy31415@gmail.com"

        omokBoardView = findViewById(R.id.omokBoard);
        concedeButton = findViewById(R.id.concedeButton);

        omokBoardView.setOnTouchListener { view, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_UP -> {
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    println("[$x, $y]")
                }
                else -> {

                }
            }
            true
        }

        concedeButton.setOnClickListener {
            val dialog = Dialog(this)

            dialog.setContentView(R.layout.concede);

            val concedeYesButton = dialog.findViewById<Button>(R.id.concedeYes);
            val concedeNoButton = dialog.findViewById<Button>(R.id.concedeNo);

            concedeYesButton.setOnClickListener {
                println("Concede YES")
                dialog.dismiss()
            }

            concedeNoButton.setOnClickListener {
                println("Concede NO")
                dialog.dismiss()
            }

            dialog.show()
        }

        queue = Volley.newRequestQueue(this)

        val log = "$ip/user/email/$email"
        val myInfoRequest = JsonArrayRequest(
            Request.Method.GET,
            log,null,
            { response ->
                myNicknameTextView.text = response.getJSONObject(0).getString("nickname")
                myRatingTextView.text = response.getJSONObject(0).getString("elo_rating")
                myNickname = myNicknameTextView.text.toString()
                myRating = myRatingTextView.text.toString().toInt()
                mySchool = response.getJSONObject(0).getString("school")
                val wsConUrl = "$serverUrl?nickname=$myNickname&elo_rating=$myRating&school=$mySchool"
                WebSocketManager.init(wsConUrl, this)
                WebSocketManager.connect()
            },
            {}
        )

        myInfoRequest.setShouldCache(false)
        queue.add(myInfoRequest)
    }

    override fun onConnectSuccess() {

    }

    override fun onConnectFailed() {
    }

    override fun onClose() {
    }

    override fun onMessage(text: String?) {
        if (text == "WAITING") {
            println(text)
        }
        else {
            /*
            {
                "gameId": uuid
                "player": 1 or 2
                "opponent": {
                    "nickname": string
                    "elo_rating": int
                    "school": string
                }
            }
             */
            println(text)
            val msg = JSONTokener(text).nextValue() as JSONObject
            println(msg)
            val opponent = msg.getJSONObject("opponent")
            println(opponent)
            val opponentNickname = opponent.getString("nickname")
            println(opponentNickname)
            val opponentRating = opponent.getInt("elo_rating")
            println(opponentRating)
            val opponentSchool = opponent.getString("school")
            println(opponentSchool)

            findViewById<TextView>(R.id.opponentNicknameTextView).text = opponentNickname
            findViewById<TextView>(R.id.opponentRatingTextView).text = opponentRating.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy ()
        WebSocketManager.close()
    }
}