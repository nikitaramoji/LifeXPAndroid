package hu.ait.lifexp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_habit_info.*
import android.widget.EditText



class HabitInfoActivity : AppCompatActivity() {

    var lifeExpNum = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_info)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("NIKITA", "DocumentSnapshot data: ${document.data}")
                    lifeExpNum = document.data?.get("demographicExpectancy").toString().toInt()
                } else {
                    Log.d("NIKITA", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("NIKITA", "get failed with ", exception)
            }

        btnUpdateLifeXP.setOnClickListener {
            if(checkAllFieldsFilled()) {
                updateLifeXP()
            }
        }
    }

    private fun checkAllFieldsFilled(): Boolean {
        var allFieldsFilled = true
        if(isEmpty(etExercise)) {
            etExercise.setError("This field cannot be empty")
            allFieldsFilled = false
        }

        if(isEmpty(etFastFood)) {
            etFastFood.setError("This field cannot be empty")
            allFieldsFilled = false
        }

        if(isEmpty(etSleep)) {
            etSleep.setError("This field cannot be empty")
            allFieldsFilled = false
        }

        if(isEmpty(etWork)) {
            etWork.setError("This field cannot be empty")
            allFieldsFilled = false
        }
        return allFieldsFilled
    }

    private fun isEmpty(etText: EditText): Boolean {
        return if (etText.text.toString().trim { it <= ' ' }.length > 0) false else true
    }

    fun updateLifeXP() {
        lifeExpNum += (updateByExercise() + updateByFastFood() + updateBySleep() + updateByWork())
        tvUpdatedLifeExpNum.text = lifeExpNum.toString()
        btnContinue.visibility = View.VISIBLE
    }

    fun updateByExercise(): Int {
        val exerciseDays: Int = Integer.parseInt(etExercise.text.toString())
        if(exerciseDays <= 0) {
            return -7
        } else if(exerciseDays <= 2) {
            return 0
        } else if(exerciseDays == 3) {
            return 1
        } else if(exerciseDays == 4) {
            return 2
        } else if(exerciseDays == 5) {
            return 3
        } else {
            return 5
        }
    }

    fun updateByFastFood(): Int {
        val fastFood: Int = Integer.parseInt(etExercise.text.toString())
        if(fastFood <= 0) {
            return 1
        } else if(fastFood <= 2) {
            return -1
        } else if(fastFood <= 4) {
            return -4
        } else {
            return -8
        }

    }

    fun updateBySleep(): Int {
        val sleepHours: Int = Integer.parseInt(etExercise.text.toString())
        if(sleepHours < 5) {
            return -4
        } else if(sleepHours <= 6) {
            return -3
        } else if(sleepHours <= 7) {
            return 1
        } else if(sleepHours <=9)
            return 2
        else {
            return -1
        }
    }

    fun updateByWork(): Int {
        val workHours: Int = Integer.parseInt(etExercise.text.toString())
        if(workHours == 0) {
            return 0
        } else if(workHours < 40) {
            return 1
        } else if(workHours <= 80) {
            return -1
        } else {
            return -2
        }
    }
}
