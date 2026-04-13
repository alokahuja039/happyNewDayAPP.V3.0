package com.habittracker.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class SplashActivity : AppCompatActivity() {

    private val DURATION = 12_000L
    private val handler  = Handler(Looper.getMainLooper())

    private val quotes = listOf(
        "The secret of getting ahead is getting started."                           to "Mark Twain",
        "It always seems impossible until it's done."                               to "Nelson Mandela",
        "Don't watch the clock; do what it does. Keep going."                       to "Sam Levenson",
        "Believe you can and you're halfway there."                                 to "Theodore Roosevelt",
        "In the middle of every difficulty lies opportunity."                       to "Albert Einstein",
        "It does not matter how slowly you go as long as you do not stop."          to "Confucius",
        "Everything you've ever wanted is on the other side of fear."               to "George Addair",
        "Success is not final, failure is not fatal: the courage to continue counts." to "Winston Churchill",
        "Be the change you wish to see in the world."                               to "Mahatma Gandhi",
        "The harder the battle, the sweeter the victory."                           to "Les Brown",
        "Do something today that your future self will thank you for."              to "Sean Patrick Flanery",
        "Don't stop when you're tired. Stop when you're done."                      to "Banksy",
        "Push yourself, because no one else is going to do it for you."             to "Unknown",
        "Great things never come from comfort zones."                               to "Unknown",
        "Wake up with determination. Go to bed with satisfaction."                  to "Unknown",
        "Quality is not an act, it is a habit."                                     to "Aristotle",
        "We become what we repeatedly do."                                          to "Sean Covey",
        "Small daily improvements are the key to staggering long-term results."     to "Unknown",
        "Dream it. Believe it. Build it."                                           to "Unknown",
        "Your only limit is your mind."                                             to "Unknown"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val q   = quotes.random()
        val day = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY    -> "Monday"
            Calendar.TUESDAY   -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY  -> "Thursday"
            Calendar.FRIDAY    -> "Friday"
            Calendar.SATURDAY  -> "Saturday"
            else               -> "Sunday"
        }

        findViewById<TextView>(R.id.splashGreeting).text = "Have a Happy"
        findViewById<TextView>(R.id.splashDay).text      = day
        findViewById<TextView>(R.id.splashQuote).text    = "\u201c${q.first}\u201d"
        findViewById<TextView>(R.id.splashAuthor).text   = "\u2014 ${q.second}"

        val pb    = findViewById<ProgressBar>(R.id.splashProgress).apply { max = 1000 }
        val start = System.currentTimeMillis()
        val tick  = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - start
                pb.progress = ((elapsed.toFloat() / DURATION) * 1000).toInt().coerceAtMost(1000)
                if (elapsed < DURATION) handler.postDelayed(this, 40)
            }
        }
        handler.post(tick)
        handler.postDelayed({ goToMain() }, DURATION)

        findViewById<Button>(R.id.skipBtn).setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            goToMain()
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
