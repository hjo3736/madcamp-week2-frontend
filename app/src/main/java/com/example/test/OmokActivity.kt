package com.example.test

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
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

class OmokActivity : AppCompatActivity() {
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

    // websocket stuff
    private lateinit var client: OkHttpClient
    private lateinit var ws: WebSocket

    private inner class OmokWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
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
                        val opponent = msg.getJSONObject("opponent")
                        val opponentNickname = opponent.getString("nickname")
                        val opponentRating = opponent.getInt("elo_rating")
                        val opponentSchool = opponent.getString("school")
                        gameId = msg.getString("gameId")
                        println(gameId)

                        myTurn = msg.getInt("player") == 1

                        // 선공
                        if (myTurn) {
                            player = 1
                        }
                        else {
                            player = 2
                        }

                        findViewById<ImageView>(R.id.myColor).setImageResource(getPieceColor(player))
                        findViewById<ImageView>(R.id.opponentColor).setImageResource(getPieceColor(3 - player))

                        foundGameDialog.show()

                        opponentNicknameTextView.text = opponentNickname
                        opponentRatingTextView.text = opponentRating.toString()
                        opponentSchoolImageView.setImageResource(SchoolInfo.getLogo(opponentSchool))
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
                        },
                        "newRating": int
                    }
                    */
                    runOnUiThread {
                        val moveResult = msg.getJSONObject("moveResult")
                        val lastPlayer = moveResult.getInt("player")
                        val lastMoveRow = moveResult.getInt("row")
                        val lastMoveCol = moveResult.getInt("col")
                        val status = moveResult.getInt("status")

                        if (status == 0) {
                            board[lastMoveRow][lastMoveCol] = lastPlayer
                            if (player != lastPlayer) {
                                myTurn = true
                            }

                            placeMove(lastPlayer, lastMoveRow, lastMoveCol)
                        }
                        else {
                            val dialog = Dialog(applicationContext)
                            val newRating = msg.getInt("newRating")
                            dialog.setContentView(R.layout.game_result_dialog)
                            val winOrLose = dialog.findViewById<TextView>(R.id.winOrLose)

                            if (status == 3) {
                                winOrLose.text = "무승부"
                            }
                            else {
                                winOrLose.text = if (status == player) "승리" else "패배"
                            }

                            dialog.findViewById<TextView>(R.id.myOldRating).text = myRating.toString()
                            dialog.findViewById<TextView>(R.id.myNewRating).text = newRating.toString()
                            dialog.findViewById<Button>(R.id.okButton).setOnClickListener {
                                finish()
                            }

                            dialog.show()
                        }
                    }
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, reason)
        }
    }

    fun placeMove(player: Int, row: Int, col: Int) {
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
        val mediaPlayer = MediaPlayer.create(this, R.raw.sound_cut)
        mediaPlayer.start()

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

        omokBoardView = findViewById(R.id.omokBoard);
        concedeButton = findViewById(R.id.concedeButton);

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
//                            WebSocketManager.sendMessage(gson.toJson(OmokMove(gameId, player, row, col)))
                            ws.send(gson.toJson(OmokMove(gameId, player, row, col)))
                        }
                    }
                    else -> { }
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
                val gson = Gson()
//                WebSocketManager.sendMessage(gson.toJson(OmokMove(gameId, player, -1, -1)))
                ws.send(gson.toJson(OmokMove(gameId, player, -1, -1)))
                dialog.dismiss()
            }

            concedeNoButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        queue = Volley.newRequestQueue(this)

        val log = "${IP.getIP()}/user/email/$email"
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
                    mySchoolImageView.setImageResource(SchoolInfo.getLogo(mySchool))

                    val wsConUrl = "$serverUrl?nickname=$myNickname&elo_rating=$myRating&school=$mySchool"
//                    WebSocketManager.init(wsConUrl, this)
//                    WebSocketManager.connect()

                    client = OkHttpClient()
                    val request = okhttp3.Request.Builder().url(wsConUrl).build()
                    val listener = OmokWebSocketListener()
                    ws = client.newWebSocket(request, listener)
                }
            },
            {}
        )

        myInfoRequest.setShouldCache(false)
        queue.add(myInfoRequest)

        searchGameDlg = Dialog(this)
        searchGameDlg.setContentView(R.layout.search_game_dialog)
        searchGameDlg.setCancelable(false)
        searchGameDlg.setCanceledOnTouchOutside(false)
        val cancelSearchButton = searchGameDlg.findViewById<Button>(R.id.cancelSearchButton)
        cancelSearchButton.setOnClickListener {
            finish()
        }

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

    override fun onDestroy() {
        runOnUiThread {
            super.onDestroy()
            ws.close(1000, myNickname)
        }
    }
}