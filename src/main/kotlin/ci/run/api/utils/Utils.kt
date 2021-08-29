package ci.run.api.utils

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*

object Utils {
    const val STANDARD_FORMAT = "yyyy-MM-dd"
    fun getCurrentData(): DateTime {
        return DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Asia/Shanghai")))
    }
}