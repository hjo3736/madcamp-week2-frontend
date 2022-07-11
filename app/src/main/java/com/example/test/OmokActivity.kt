package com.example.test

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.Exception
import kotlin.math.roundToInt

val boardSize = 11

class OmokMove(val gameId: String, val player: Int, val row: Int, val col: Int)

fun getPieceColor(player: Int): Int {
    when (player) {
        1 -> return R.drawable.black_circle
        2 -> return R.drawable.white_circle
    }
    throw Exception()
}

class OmokActivity : AppCompatActivity(), MessageListener {
    private val serverUrl = "ws://192.249.18.128/websockets"

    // game stuff
    private lateinit var myNickname: String
    private lateinit var mySchool: String
    private var myRating: Int = 0
    private var myTurn: Boolean = false
    private var board = Array(boardSize) { Array(boardSize) { 0 } }
    private lateinit var gameId: String
    private var player = 0

    // UI stuff
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val boardWidth = (screenWidth * 0.95).roundToInt()
    private lateinit var omokBoardView: ImageView
    private lateinit var concedeButton: ImageButton
    private lateinit var myNicknameTextView: TextView
    private lateinit var myRatingTextView: TextView
    private lateinit var mySchoolImageView: ImageView
    private lateinit var opponentNicknameTextView: TextView
    private lateinit var opponentRatingTextView: TextView
    private lateinit var opponentSchoolImageView: ImageView
    private lateinit var searchGameDlg: Dialog
    private lateinit var foundGameDialog: Dialog

    fun placeMove(player: Int, row: Int, col: Int, ) {
        val imageView = ImageView(this)
        imageView.setImageResource(getPieceColor(player))
        imageView.requestLayout()

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        imageView.layoutParams = ConstraintLayout.LayoutParams(
            boardWidth / boardSize,
            boardWidth / boardSize
        )

        imageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = omokBoardView.id
            topToTop = omokBoardView.id
            marginStart = col * (boardWidth / boardSize)
            topMargin = row * (boardWidth / boardSize)
        }

        constraintLayout.addView(imageView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_omok)

        myNicknameTextView = findViewById(R.id.myNicknameTextView)
        myRatingTextView = findViewById(R.id.myRatingTextView)
        mySchoolImageView = findViewById(R.id.mySchoolImageView)
        opponentNicknameTextView = findViewById(R.id.opponentNicknameTextView)
        opponentRatingTextView = findViewById(R.id.opponentRatingTextView)
        opponentSchoolImageView = findViewById(R.id.opponentSchoolImageView)

        val email = intent.getStringExtra("email")
//        val email = "leejy31415@gmail.com"

        omokBoardView = findViewById(R.id.omokBoard);
        concedeButton = findViewById(R.id.concedeButton);
//        recyclerView = findViewById(R.id.recyclerView)

        omokBoardView.setOnTouchListener { view, event ->
            if (!myTurn)
                true
            else {
                val action = event.action
                when (action) {
                    MotionEvent.ACTION_UP -> {
                        val col = (event.x.toInt() * (boardSize) / boardWidth)
                        val row = (event.y.toInt() * (boardSize) / boardWidth)
                        println("row = $row\t col=$col")
                        if (board[row][col] == 0) {
                            myTurn = false
                            val gson = Gson()
                            println(gson.toJson(OmokMove(gameId, player, row, col)))
                            WebSocketManager.sendMessage(gson.toJson(OmokMove(gameId, player, row, col)))
                        }
                    }
                    else -> {

                    }
                }
                true
            }
        }

        concedeButton.setOnClickListener {
            val dialog = Dialog(this)

            dialog.setContentView(R.layout.concede_dialog);

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
                this.runOnUiThread {
                    myNicknameTextView.text = response.getJSONObject(0).getString("nickname")
                    myRatingTextView.text = response.getJSONObject(0).getString("elo_rating")
                    myNickname = myNicknameTextView.text.toString()
                    myRating = myRatingTextView.text.toString().toInt()

                    mySchool = response.getJSONObject(0).getString("school")
                    mySchoolImageView.setImageResource(SchoolLogos.getLogo(mySchool))

                    val wsConUrl = "$serverUrl?nickname=$myNickname&elo_rating=$myRating&school=$mySchool"
                    WebSocketManager.init(wsConUrl, this)
                    WebSocketManager.connect()
                }
            },
            {}
        )

        myInfoRequest.setShouldCache(false)
        queue.add(myInfoRequest)

        searchGameDlg = Dialog(this)
        searchGameDlg.setContentView(R.layout.search_game_dialog)
        searchGameDlg.setCanceledOnTouchOutside(false)

        foundGameDialog = Dialog(this)
        foundGameDialog.setContentView(R.layout.found_game_dialog)
        foundGameDialog.setCanceledOnTouchOutside(false)
        val btn = foundGameDialog.findViewById<Button>(R.id.foundGameCloseBtn)
        btn.setOnClickListener {
            foundGameDialog.dismiss()
        }

        omokBoardView.layoutParams.width = boardWidth
        omokBoardView.layoutParams.height = boardWidth
    }

    override fun onConnectSuccess() {}

    override fun onConnectFailed() {}

    override fun onClose() {}

    override fun onMessage(text: String?) {
        val msg = JSONTokener(text).nextValue() as JSONObject

        when (msg.getString("type")) {
            "waiting" -> {
                runOnUiThread {
                    searchGameDlg.show()
                }
            }
            "gameFound" -> {
                /*
                {
                    "type": "gameFound",
                    "gameId": string(uuid),
                    "player": 1 or 2,
                    "opponent": {
                        "nickname": string,
                        "elo_rating": int,
                        "school": string
                    }
                }
                */
                runOnUiThread {
                    if (!searchGameDlg.isShowing) {
                        searchGameDlg.show()
                        Thread.sleep(1000)
                    }

                    val opponent = msg.getJSONObject("opponent")
                    val opponentNickname = opponent.getString("nickname")
                    val opponentRating = opponent.getInt("elo_rating")
                    val opponentSchool = opponent.getString("school")
                    gameId = msg.getString("gameId")
                    println(gameId)

                    myTurn = msg.getInt("player") == 1

                    // 선공
                    if (myTurn) {
                        myNicknameTextView.setTypeface(myNicknameTextView.typeface, Typeface.BOLD)
                        player = 1
                    }
                    else {
                        opponentNicknameTextView.setTypeface(opponentNicknameTextView.typeface, Typeface.BOLD)
                        player = 2
                    }

                    findViewById<ImageView>(R.id.myColor).setImageResource(getPieceColor(player))
                    findViewById<ImageView>(R.id.opponentColor).setImageResource(getPieceColor(3 - player))

                    searchGameDlg.dismiss()
                    foundGameDialog.show()

                    opponentNicknameTextView.text = opponentNickname
                    opponentRatingTextView.text = opponentRating.toString()
                    opponentSchoolImageView.setImageResource(SchoolLogos.getLogo(opponentSchool))
                }
            }
            "moveResult" -> {
                /*
                {
                    "type": "moveResult",
                    "moveResult": {
                        "player": 1 or 2,
                        "row": int,
                        "col": int,
                        "status": 0, 1, 2 or 3
                    }
                }
                */
                runOnUiThread {
                    val moveResult = msg.getJSONObject("moveResult")
                    val lastPlayer = moveResult.getInt("player")
                    val lastMoveRow = moveResult.getInt("row")
                    val lastMoveCol = moveResult.getInt("col")
                    this.board[lastMoveRow][lastMoveCol] = lastPlayer
                    if (this.player != lastPlayer) {
                        myTurn = true
                    }

                    placeMove(lastPlayer, lastMoveRow, lastMoveCol)
                }
            }

            "newRating" -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy ()
        WebSocketManager.close()
    }
}