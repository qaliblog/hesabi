package com.qali.hesabi.util

fun String.toEnglishNumbers(): String {
    var result = this
    val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    persianNumbers.forEachIndexed { index, persianNumber ->
        result = result.replace(persianNumber, index.toString())
    }
    return result
}
