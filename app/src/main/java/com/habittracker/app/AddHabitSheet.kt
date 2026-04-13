package com.habittracker.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class AddHabitSheet : BottomSheetDialogFragment() {

    var onSave: ((Habit) -> Unit)? = null
    private var editing: Habit? = null
    private var dir    = "do_more"
    private var period = "daily"
    private var count  = 1

    private val moreFacts = listOf(
        "People who track habits are 42% more likely to achieve their goals.",
        "Doing something just 21 days in a row starts to rewire your brain.",
        "Small daily actions compound into extraordinary results over time.",
        "The habit you build today is the person you become tomorrow.",
        "Showing up consistently, even imperfectly, is the real secret weapon.",
        "You don't rise to your goals — you fall to the level of your systems."
    )
    private val lessFacts = listOf(
        "Reducing a bad habit by even 20% can significantly improve wellbeing.",
        "Awareness is the first step — tracking keeps you honest with yourself.",
        "Progress, not perfection. Every day you resist is a win.",
        "What gets measured gets managed. Start tracking today.",
        "Breaking habits takes time, but your brain is always rewiring itself."
    )

    companion object {
        fun forHabit(h: Habit?) = AddHabitSheet().apply { editing = h }
    }

    override fun getTheme() = R.style.BottomSheetTheme

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.sheet_add_habit, container, false)

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val title    = v.findViewById<TextView>(R.id.sheetTitle)
        val nameIn   = v.findViewById<EditText>(R.id.habitNameInput)
        val doMore   = v.findViewById<TextView>(R.id.doMoreBtn)
        val doLess   = v.findViewById<TextView>(R.id.doLessBtn)
        val factCard = v.findViewById<LinearLayout>(R.id.factCard)
        val factTv   = v.findViewById<TextView>(R.id.factText)
        val daily    = v.findViewById<TextView>(R.id.dailyBtn)
        val weekly   = v.findViewById<TextView>(R.id.weeklyBtn)
        val monthly  = v.findViewById<TextView>(R.id.monthlyBtn)
        val minus    = v.findViewById<ImageButton>(R.id.goalMinus)
        val plus     = v.findViewById<ImageButton>(R.id.goalPlus)
        val cntTv    = v.findViewById<TextView>(R.id.goalCountText)
        val goalLbl  = v.findViewById<TextView>(R.id.goalLabel)
        val saveBtn  = v.findViewById<TextView>(R.id.saveBtn)
        val closeBtn = v.findViewById<ImageButton>(R.id.closeBtn)

        title.text = if (editing != null) "Edit Habit" else "Add New Habit"
        editing?.let {
            nameIn.setText(it.name)
            dir = it.goalDirection; period = it.trackingPeriod; count = it.goalCount
        }
        cntTv.text = count.toString()

        fun refreshDir() {
            val isMore = dir == "do_more"
            doMore.setBackgroundResource(if (isMore) R.drawable.toggle_on else R.drawable.toggle_off)
            doLess.setBackgroundResource(if (!isMore) R.drawable.toggle_on else R.drawable.toggle_off)
            doMore.setTextColor(if (isMore) 0xFFFFFFFF.toInt() else 0xFF1C1C1E.toInt())
            doLess.setTextColor(if (!isMore) 0xFFFFFFFF.toInt() else 0xFF1C1C1E.toInt())
            factTv.text = if (isMore) moreFacts.random() else lessFacts.random()
            factCard.visibility = View.VISIBLE
            goalLbl.text = if (isMore) "Goal per period" else "Maximum per period"
        }

        fun refreshPeriod() {
            listOf(daily to "daily", weekly to "weekly", monthly to "monthly").forEach { (btn, p) ->
                btn.setBackgroundResource(if (period == p) R.drawable.toggle_on else R.drawable.toggle_off)
                btn.setTextColor(if (period == p) 0xFFFFFFFF.toInt() else 0xFF1C1C1E.toInt())
            }
        }

        doMore.setOnClickListener  { dir = "do_more"; refreshDir() }
        doLess.setOnClickListener  { dir = "do_less"; refreshDir() }
        daily.setOnClickListener   { period = "daily";   refreshPeriod() }
        weekly.setOnClickListener  { period = "weekly";  refreshPeriod() }
        monthly.setOnClickListener { period = "monthly"; refreshPeriod() }
        minus.setOnClickListener   { if (count > 1)  { count--; cntTv.text = count.toString() } }
        plus.setOnClickListener    { if (count < 99) { count++; cntTv.text = count.toString() } }
        closeBtn.setOnClickListener { dismiss() }

        saveBtn.setOnClickListener {
            val name = nameIn.text.toString().trim()
            if (name.isEmpty()) { nameIn.error = "Enter a habit name"; return@setOnClickListener }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            onSave?.invoke(
                Habit(
                    id             = editing?.id ?: UUID.randomUUID().toString(),
                    name           = name,
                    goalDirection  = dir,
                    trackingPeriod = period,
                    goalCount      = count,
                    startDate      = editing?.startDate ?: today,
                    completedDates = editing?.completedDates ?: mutableMapOf()
                )
            )
            dismiss()
        }

        if (editing == null) factCard.visibility = View.GONE
        refreshDir()
        refreshPeriod()
    }
}
