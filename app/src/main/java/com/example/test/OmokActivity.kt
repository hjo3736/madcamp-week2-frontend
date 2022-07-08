package com.example.test

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView

class OmokActivity : AppCompatActivity() {
    lateinit var omokBoardView: ImageView
    lateinit var concedeButton: ImageButton

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_omok)

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
    }

    fun f(view: View) {

    }
}