package com.example.opsc_part2

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class custom_dashboard_cards @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.custom_dashboard_cards, this, true)

        // Perform any initialization or customization here
        // You can access and modify the views within the custom component layout
    }

    // Used to set activity Name
    fun setActivityName(name: String) {
        // Update the activity name view
        val activityName = findViewById<TextView>(R.id.tvActivityName)

        activityName.text = name;
    }

    // Used to set activity start Date
    fun setActivityStartDate(startDate: String) {
        val actStartDate = findViewById<TextView>(R.id.tvDateCreated)

        actStartDate.text = startDate;
    }

    // Used to set the background color of card
    fun setCardColor(selectedColor: String) {
        val colorResource = when (selectedColor) {
            "purple" -> R.color.purple_500
            "blue" -> androidx.appcompat.R.color.material_blue_grey_800
            "green" -> R.color.teal_200
            else -> R.color.black // Replace with your default color resource ID
        }
        val colorToSet = ContextCompat.getColorStateList(context, colorResource)
        val cardLayout = findViewById<RelativeLayout>(R.id.relCard)
        cardLayout.backgroundTintList = colorToSet
    }
}
// These methods are called within the dahsboard activity