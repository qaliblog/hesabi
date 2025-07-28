package com.qali.hesabi.util

import java.util.Date
import java.util.GregorianCalendar

object JalaliUtils {
    fun toJalaliString(date: Date): String {
        // Simple fallback: just show the Gregorian date as a placeholder
        val cal = GregorianCalendar()
        cal.time = date
        val year = cal.get(GregorianCalendar.YEAR)
        val month = (cal.get(GregorianCalendar.MONTH) + 1).toString().padStart(2, '0')
        val day = cal.get(GregorianCalendar.DAY_OF_MONTH).toString().padStart(2, '0')
        return "$year/$month/$day (Gregorian)" // Replace with real Jalali conversion if needed
    }
}