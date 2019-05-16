package hu.ait.lifexp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_habit_info.*

class HabitInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_info)

        btnUpdateLifeXP.setOnClickListener {
            updateLifeXP()
        }
    }

    fun updateLifeXP() {

    }



}
