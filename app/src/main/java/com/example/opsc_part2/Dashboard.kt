package com.example.opsc_part2

import Classes.ToolBox
import TimerManager
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.min

class Dashboard : AppCompatActivity(), QuickActionPopup.DashboardFragmentListener {

    //ui vars
    private lateinit var actionButt: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAddActivity: FloatingActionButton
    private lateinit var fabAddCategory: FloatingActionButton
    private lateinit var buttonsLayout: View
    private var isFabExpanded = false

    //============================================================================
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // ======================= Declarations ======================= //
        bottomNav = findViewById(R.id.bottomNavView)
        actionButt = findViewById<FloatingActionButton>(R.id.btnPlus)
        fabAddActivity = findViewById(R.id.fab_add_activity)
        fabAddCategory = findViewById(R.id.fab_add_category)
        buttonsLayout = findViewById(R.id.buttonsLayout)

        val linView = findViewById<LinearLayout>(R.id.linearProjectCards)

        val fragment = QuickActionPopup()
        // Var to hold fragment visibility state
        var isFragmentVisible = false

        // Obtain a reference to the ImageView
        val imgProfileImg = findViewById<ImageView>(R.id.imgProfileImg)
        // Create a Bitmap from the image drawable
        // This is where you change size of image
        val maxImageSize = 140
        // ======================= End Declarations ======================= //
        imgProfileImg.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        // Create a Bitmap from the image drawable
        val drawable = resources.getDrawable(R.drawable.temp_profilepicture) as BitmapDrawable
        val bitmap = drawable.bitmap

        // Calculate the desired size for the circular ImageView, considering the maximum size
        val imageSize = min(bitmap.width, bitmap.height)
        val scaleFactor = maxImageSize.toFloat() / imageSize.toFloat()
        val targetSize = (imageSize * scaleFactor).toInt()

        // Resize the original image bitmap to the target size
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, true)

        // Create a circular Bitmap with the desired size
        val circularBitmap = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circularBitmap)

        // Create a Paint object to define the circular shape
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(resizedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        // Draw a circular shape on the canvas using the Paint
        val radius = targetSize / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        // Set the circular Bitmap as the new image for the ImageView
        imgProfileImg.setImageBitmap(circularBitmap)

        // Set the dimensions of the ImageView to match the circular Bitmap
        val params = RelativeLayout.LayoutParams(targetSize, targetSize)
        params.leftMargin = 50
        params.topMargin = 25
        imgProfileImg.layoutParams = params

        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Menu_Stats -> {
                    val fragmentManager = supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.add(R.id.container, Statistics())
                    transaction.commit()
                    true
                }
                R.id.Menu_Dashboard -> {
                    val intent = Intent(this, Dashboard::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
                    ActivityCompat.startActivity(this, intent, options.toBundle())
                    true
                }
                R.id.Menu_Settings -> {
                    val intent = Intent(this, Settings::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
                    ActivityCompat.startActivity(this, intent, options.toBundle())
                    true
                }
                R.id.Menu_Logs -> {
                    val fragmentManager = supportFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.add(R.id.container, Logs())
                    transaction.commit()
                    true
                }
                else -> false
            }
        }

        // ----------------- Creating a new card with custom attributes ----------------- //
        for (card in ToolBox.ActivitiesList) {
            val customCard = custom_dashboard_cards(this)
            customCard.setActivityName(card.ActivityName)
            customCard.setActivityStartDate(card.DateCreated)
            customCard.setCardColor(card.ActivityColor) // not dynamically added
            customCard.setActivityMinGoal("Min Goal: " + card.ActivityMinGoal)
            customCard.setActivityMaxGoal("Max Goal: " + card.ActivityMaxGoal)

            val timerText = customCard.findViewById<TextView>(R.id.txtTimerTick)
            timerText.text = "00:00:00";

            val play = customCard.findViewById<ImageButton>(R.id.ibPausePlay)
            val completeActivity = customCard.findViewById<ImageButton>(R.id.ibFinsih)

            play.setOnClickListener {
                TimerManager.startTimer(customCard, timerText)
            }

            completeActivity.setOnClickListener {
                val fragment = complete_activity()

                // put data into fragment
                val args = Bundle()

                args.putString("color", card.ActivityColor)
                args.putString("duration", "2")
                args.putInt("id", card.ActivityID)
                args.putString("name", card.ActivityName)

                var a = 0

                fragment.arguments = args
                fragment.show(supportFragmentManager, "completeActivity")
            }

            linView.addView(customCard)
        }


        /*
        * If fragment is visible, hide when button is clicked
        * Else if fragment is not visible when button clicked, then show fragment
        * */
        actionButt.setOnClickListener {
            actionButt.setOnClickListener {
                if (isFabExpanded) {
                    collapseFabMenu()
                } else {
                    expandFabMenu()
                }
            }

            //showPopup()
            //isFragmentVisible = true  // Setting visible to true if fragment is shown | Was only used with other load method
        }
        fabAddActivity.setOnClickListener {
            // Handle add activity button click
        }

        fabAddCategory.setOnClickListener {
            // Handle add category button click
        }
    }

    private fun expandFabMenu() {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(actionButt, "rotation", 0f, 45f),
            ObjectAnimator.ofFloat(fabAddActivity, "translationY", 0f, -resources.getDimension(R.dimen.fab_margin)),
            ObjectAnimator.ofFloat(fabAddCategory, "translationY", 0f, -2 * resources.getDimension(R.dimen.fab_margin)),
        )
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.duration = 300
        buttonsLayout.visibility = View.VISIBLE
        animatorSet.start()

        isFabExpanded = true
    }

    private fun collapseFabMenu() {
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
            ObjectAnimator.ofFloat(actionButt, "rotation", 45f, 0f),
            ObjectAnimator.ofFloat(fabAddActivity, "translationY", -resources.getDimension(R.dimen.fab_margin), 0f),
            ObjectAnimator.ofFloat(fabAddCategory, "translationY", -2 * resources.getDimension(R.dimen.fab_margin), 0f),
        )
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.duration = 300
        buttonsLayout.visibility = View.GONE
        animatorSet.start()

        isFabExpanded = false
    }

    //============================================================================
    override fun onFragmentRequested(fragment: Fragment) {
        // Replace the current fragment on the dashboard with the requested fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    //============================================================================
    private fun showPopup() {
        val fragment = QuickActionPopup()
        fragment.show(supportFragmentManager, "QuickActionPopup")
    }
}