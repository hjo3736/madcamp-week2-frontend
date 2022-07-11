package com.example.test

object SchoolLogos {
    fun getLogo(school: String):Int {
        when (school) {
            "KAIST" -> return R.mipmap.kaist
            "POSTECH" -> return R.drawable.postech
            "HYU" -> return R.drawable.hyu
            "SKKU" -> return R.drawable.skku
            "GIST" -> return R.drawable.gist
            "UNIST" -> return R.drawable.unist
        }
        return R.drawable.unknown
    }
}