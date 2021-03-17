package com.barry.kotlin_code_base.tools

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

object DatePickerUtil {

    fun showDatePickerDialog(context: Context,
                             onDateSetListener : DatePickerDialog.OnDateSetListener,
                             calendar: Calendar,
                             todayIsMinDate: Boolean,
                             todayIsMaxDate : Boolean) {
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        var datePickerDialog = DatePickerDialog(context,
            DatePickerDialog.THEME_HOLO_DARK,
            onDateSetListener,
            year, month, day)
        if (todayIsMinDate) {
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
        }
        if (todayIsMaxDate) {
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        }
        datePickerDialog.show()
    }

    fun setDateFormat(year : Int, monthOfYear : Int, dayOfMonth : Int) : String {
        val finalYear = year.toString()
        val finalMonth: String
        val finalDay: String

        finalMonth = if (monthOfYear < 10) {
            if (monthOfYear == 9) {
                (monthOfYear + 1).toString()
            } else {
                "0" + (monthOfYear + 1).toString()
            }
        } else {
            (monthOfYear + 1).toString()
        }

        finalDay = if (dayOfMonth < 10) {
            "0$dayOfMonth"
        } else {
            dayOfMonth.toString()
        }

        return "$finalYear-$finalMonth-$finalDay"
    }
}