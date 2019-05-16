package hu.ait.lifexp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_habit_info.*

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
            updateLifeXP()
        }
    }

    fun updateLifeXP() {
        tvUpdatedLifeExpNum.text = lifeExpNum.toString()
        btnContinue.visibility = View.VISIBLE
    }



}
