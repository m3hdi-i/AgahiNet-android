package ir.m3hdi.agahinet.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

class PersianTimeAgo {

    companion object{

        private val tehranZone by lazy{
            val offset = ZoneOffset.of("+03:30")
            ZoneId.ofOffset("UTC", offset)
        }

        private val formatter by lazy {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        }

        /**
         *  Converts DateTime string in yyyy-MM-dd HH:mm:ss.SSSSSS format to X Time ago text
         */
        fun dateTimeStringToTimeAgo(input: String): String {
            return try {
                val inputDateTime = LocalDateTime.parse(input, formatter)
                val inputZonedDateTime = inputDateTime.atZone(tehranZone)
                val currentZonedDateTime = ZonedDateTime.now(tehranZone)
                val duration = Duration.between(inputZonedDateTime, currentZonedDateTime).abs()
                val days = duration.toDays()
                when {
                    days >= 365 -> "${days / 365}"+" سال پیش"
                    days >= 30 -> "${days / 30}"+ " ماه پیش"
                    days >= 1 -> when (days)  {
                        1L -> "دیروز"
                        else -> "${days}" + " روز پیش"
                    }
                    duration.toHours() >= 1 -> "${duration.toHours()}"+" ساعت پیش"
                    duration.toMinutes() >= 1 -> "${duration.toMinutes()}"+ " دقیقه پیش"
                    else -> "لحظاتی پیش"
                }

            }catch (ex:Exception){
                ex.printStackTrace()
                "لحظاتی پیش"
            }
        }


    }

}