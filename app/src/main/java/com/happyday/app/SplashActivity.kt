package com.happyday.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class SplashActivity : AppCompatActivity() {

    private val SPLASH_MS = 12_000L
    private val handler = Handler(Looper.getMainLooper())

    private val quotes = listOf(
        "The secret of getting ahead is getting started." to "Mark Twain",
        "It always seems impossible until it's done." to "Nelson Mandela",
        "Don't watch the clock; do what it does. Keep going." to "Sam Levenson",
        "Believe you can and you're halfway there." to "Theodore Roosevelt",
        "In the middle of every difficulty lies opportunity." to "Albert Einstein",
        "It does not matter how slowly you go as long as you do not stop." to "Confucius",
        "Everything you've ever wanted is on the other side of fear." to "George Addair",
        "Success is not final, failure is not fatal: the courage to continue is what counts." to "Winston Churchill",
        "Hardships often prepare ordinary people for an extraordinary destiny." to "C.S. Lewis",
        "Be the change you wish to see in the world." to "Mahatma Gandhi",
        "The harder the battle, the sweeter the victory." to "Les Brown",
        "Do something today that your future self will thank you for." to "Sean Patrick Flanery",
        "Don't stop when you're tired. Stop when you're done." to "Banksy",
        "Push yourself, because no one else is going to do it for you." to "Unknown",
        "Great things never come from comfort zones." to "Unknown",
        "Dream it. Believe it. Build it." to "Unknown",
        "Wake up with determination. Go to bed with satisfaction." to "Unknown",
        "Little things make big days." to "Unknown",
        "Don't wait for opportunity. Create it." to "Unknown",
        "Sometimes we're tested not to show our weaknesses, but to discover our strengths." to "Unknown",
        "The key to success is to focus on goals, not obstacles." to "Unknown",
        "Success doesn't just find you. You have to go out and get it." to "Unknown",
        "Believe in yourself. You are braver than you think." to "Unknown",
        "Your only limit is your mind." to "Unknown",
        "Sometimes later becomes never. Do it now." to "Unknown",
        "You don't have to be great to start, but you have to start to be great." to "Zig Ziglar",
        "The future depends on what you do today." to "Mahatma Gandhi",
        "Quality is not an act, it is a habit." to "Aristotle",
        "We become what we repeatedly do." to "Sean Covey",
        "Small daily improvements are the key to staggering long-term results." to "Unknown"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val quote = quotes.random()
        val dayName = getDayName()

        findViewById<TextView>(R.id.splashGreeting).text = "Have a Happy"
        findViewById<TextView>(R.id.splashDay).text = dayName
        findViewById<TextView>(R.id.splashQuote).text = "\u201c${quote.first}\u201d"
        findViewById<TextView>(R.id.splashAuthor).text = "\u2014 ${quote.second}"

        val progressBar = findViewById<ProgressBar>(R.id.splashProgress)
        progressBar.max = 1000

        val startTime = System.currentTimeMillis()
        val progressRunnable = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                progressBar.progress = ((elapsed.toFloat() / SPLASH_MS) * 1000).toInt().coerceAtMost(1000)
                if (elapsed < SPLASH_MS) handler.postDelayed(this, 40)
            }
        }
        handler.post(progressRunnable)

        handler.postDelayed({ goToMain() }, SPLASH_MS)

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

    private fun getDayName(): String = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY    -> "Monday"
        Calendar.TUESDAY   -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY  -> "Thursday"
        Calendar.FRIDAY    -> "Friday"
        Calendar.SATURDAY  -> "Saturday"
        Calendar.SUNDAY    -> "Sunday"
        else               -> "Day"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
