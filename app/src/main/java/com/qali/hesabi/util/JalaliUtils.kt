package com.qali.hesabi.util

import com.github.eloyzone.jalalicalendar.JalaliCalendar
import java.util.Date
import java.util.GregorianCalendar

object JalaliUtils {
    fun toJalaliString(date: Date): String {
        val cal = GregorianCalendar()
        cal.time = date
        val jalali = JalaliCalendar(cal)
        return "${jalali.year}/${jalali.monthValue.toString().padStart(2, '0')}/${jalali.dayOfMonth.toString().padStart(2, '0')}"
    }
}