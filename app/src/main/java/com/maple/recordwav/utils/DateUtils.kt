package com.maple.recordwav.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author maple
 * @time 2016/5/30
 */
object DateUtils {

    /**
     * 日期按规定格式输出
     *
     * @param fmt 指定格式，如yyyy-MM-dd HH:mm:ss
     * @return
     */
    @JvmStatic
    fun date2Str(fmt: String): String {
        return date2Str(Date(), fmt)
    }

    /**
     * 日期按规定格式输出
     *
     * @param date
     * @param fmt  指定格式，如yyyy-MM-dd HH:mm:ss
     * @return
     */
    @JvmStatic
    fun date2Str(date: Date, fmt: String): String {
        val dateFormat = SimpleDateFormat(fmt, Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * 把字符串转为日期
     *
     * @param strDate
     * @param fmt
     * @return
     */
    @JvmStatic
    fun str2Date(strDate: String, fmt: String): Date? {
        val df = SimpleDateFormat(fmt, Locale.getDefault())
        return df.parse(strDate)
    }

}
