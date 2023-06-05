package Classes

import com.example.opsc_part2.R
import java.time.LocalDate
import java.util.*

class ActivityObject(
    val ActivityID: Int,
    val ActivityUserID: Int,
    val ActivityName: String,
    val ActivityCatagory: String,
    val DateCreated: String,
    val ActivityMinGoal: Int,
    val ActivityMaxGoal: Int,
    //val ActivityIcon: String
    val ActivityColor: String,
    var timer: Timer? = null,
    ) {
}