package com.example.test

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

lateinit var queue : RequestQueue

var data = ArrayList<LeaderboardData>()
lateinit var context1: Context

class LeaderBoard : AppCompatActivity() {

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        context1 = context
        return super.onCreateView(name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)

        data = ArrayList<LeaderboardData>()

        queue = Volley.newRequestQueue(this)

        val log = IP.getIP() + "/leaderboard"

        val stringRequest = JsonArrayRequest(Request.Method.GET,
            log, null,
            Response.Listener{ response ->

                for(i in 0 until response.length()){

                    data.add(
                        LeaderboardData(
                            response.getJSONObject(i).getString("nickname"),
                            response.getJSONObject(i).getString("school"),
                            response.getJSONObject(i).getInt("elo_rating").toString()
                        )
                    )

                    Log.d("qwer", data[i].nickname)

                }

                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                val inflater = layoutInflater
                val adapter = Adapter(data, inflater)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            },
            Response.ErrorListener {error ->
                Log.d("Jsonerror", error.message.toString())
            })

        stringRequest.setShouldCache(false)
        queue.add(stringRequest)

    }

}

class Adapter (
    val data : ArrayList<LeaderboardData>,
    val inflater: LayoutInflater) : RecyclerView.Adapter<Adapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val ranking : TextView
        val nickname : TextView
        val school : ImageView
        val eloRating : TextView

        lateinit var schoolinfo : String

        init{

            ranking = itemView.findViewById(R.id.ranking)
            nickname = itemView.findViewById(R.id.nickname)
            school = itemView.findViewById(R.id.school)
            eloRating = itemView.findViewById(R.id.elo_Rating)

            itemView.setOnClickListener{

                val dialog = Dialog(context1)

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.user_info)

                val userNickname = dialog.findViewById<TextView>(R.id.forNickname)
                val userSchool = dialog.findViewById<TextView>(R.id.forSchool)
                val usereloRating = dialog.findViewById<TextView>(R.id.forelorating)
                val userScoolImage = dialog.findViewById<ImageView>(R.id.schoolimage)
                val closeButton = dialog.findViewById<ImageButton>(R.id.close)

                userNickname.setText(nickname.text.toString())
                userSchool.setText(schoolinfo)
                usereloRating.setText(eloRating.text.toString())
                userScoolImage.setImageResource(SchoolInfo.getLogo(schoolinfo))

                closeButton.setOnClickListener{

                    dialog.dismiss()

                }

                dialog.show()

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.ranking.text = (position + 1).toString()
        holder.nickname.text = data[position].nickname
        holder.schoolinfo = data[position].school
        holder.school.setImageResource(SchoolInfo.getLogo(data[position].school))
        holder.eloRating.text = data[position].elo_rating

    }

    override fun getItemCount(): Int {
        return data.size
    }


}

class LeaderboardData (val nickname : String, val school : String, val elo_rating : String)