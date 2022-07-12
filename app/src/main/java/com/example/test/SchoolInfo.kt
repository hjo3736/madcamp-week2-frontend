package com.example.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

object SchoolInfo {
    val schools = mapOf<String, Pair<String, Int>>(
        "KAIST" to Pair("한국과학기술원", R.mipmap.kaist),
        "POSTECH" to Pair("포항공과대학교", R.drawable.postech),
        "HYU" to Pair("한양대학교", R.drawable.hyu),
        "SKKU" to Pair("성균관대학교", R.drawable.skku),
        "GIST" to Pair("광주과학기술원", R.drawable.gist),
        "UNIST" to Pair("울산과학기술원", R.drawable.unist),
        "KOREA" to Pair("고려대학교", R.drawable.korea),
        "BUSAN" to Pair("부산대학교", R.drawable.busan)
    )
    fun getLogo(shortName: String):Int {
        return schools[shortName]?.second ?: R.drawable.unknown
    }

    fun getLongName(shortName: String): String {
        return schools[shortName]?.first ?: "기타"
    }
}

class School(val shortName: String)

// Huge thanks to https://www.youtube.com/watch?v=z1gPVH7PspE
class SchoolAdapter(context: Context, schools: List<School>) : ArrayAdapter<School>(context, 0, schools) {
    var layoutInflater: LayoutInflater

    init {
        this.layoutInflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val school = getItem(position)
        val rowView = layoutInflater.inflate(R.layout.school_spinner, parent, false)
        val logo = rowView.findViewById<ImageView>(R.id.spinnerSchoolLogo)
        val name = rowView.findViewById<TextView>(R.id.spinnerSchoolName)
        logo.setImageResource(SchoolInfo.getLogo(school!!.shortName))
        name.text = SchoolInfo.getLongName(school.shortName)
        return rowView
    }
}