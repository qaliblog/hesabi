package com.qali.hesabi.util

import java.util.Date
import java.util.GregorianCalendar

object JalaliUtils {
    // Converts a Gregorian date to Jalali (Shamsi) date
    fun toJalaliString(date: Date): String {
        val cal = GregorianCalendar()
        cal.time = date
        val gYear = cal.get(GregorianCalendar.YEAR)
        val gMonth = cal.get(GregorianCalendar.MONTH) + 1 // 1-based
        val gDay = cal.get(GregorianCalendar.DAY_OF_MONTH)

        val gDaysInMonth = intArrayOf(0,31,28,31,30,31,30,31,31,30,31,30,31)
        var gy = gYear - 1600
        var gm = gMonth - 1
        var gd = gDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        for (i in 0 until gm)
            gDayNo += gDaysInMonth[i + 1]
        if (gm > 1 && ((gy + 1600) % 4 == 0 && ((gy + 1600) % 100 != 0 || (gy + 1600) % 400 == 0)))
            gDayNo++
        gDayNo += gd

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        val jMonths = intArrayOf(31,31,31,31,31,31,30,30,30,30,30,29)
        var jm = 0
        var jd = 0
        for (i in 0..11) {
            if (jDayNo < jMonths[i]) {
                jm = i + 1
                jd = jDayNo + 1
                break
            }
            jDayNo -= jMonths[i]
        }

        return "%04d/%02d/%02d".format(jy, jm, jd)
    }
}