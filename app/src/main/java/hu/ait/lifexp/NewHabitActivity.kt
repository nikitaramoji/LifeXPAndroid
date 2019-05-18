package hu.ait.lifexp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import hu.ait.lifexp.data.NewHabitPost
import kotlinx.android.synthetic.main.activity_new_habit.*

class NewHabitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_habit)

        btnCreate.setOnClickListener {
            if(checkAllFieldsFilled()) {
                createNewHabit()
            }
        }
    }

    fun createNewHabit() {
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
        val habits = db.collection(
            "users"
        ).document(FirebaseAuth.getInstance().currentUser!!.uid).collection("habits")
        val post = NewHabitPost(etNewHabit.text.toString())
        habits.add(
            post
        ).addOnSuccessListener {
            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@NewHabitActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkAllFieldsFilled(): Boolean {
        var allFieldsFilled = true
        if(isEmpty(etNewHabit)) {
            etNewHabit.setError("This field cannot be empty")
            allFieldsFilled = false
        }
        return allFieldsFilled
    }

    private fun isEmpty(etText: EditText): Boolean {
        return if (etText.text.toString().trim { it <= ' ' }.length > 0) false else true
    }
}
