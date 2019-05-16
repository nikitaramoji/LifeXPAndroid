package hu.ait.lifexp


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.VERBOSE
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import hu.ait.lifexp.data.DemographicPost
import hu.ait.lifexp.data.LifeExpectancyResult
import hu.ait.lifexp.network.LifeExpectancyAPI
import kotlinx.android.synthetic.main.activity_demographic_info.*
import kotlinx.android.synthetic.main.activity_forum.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DemographicInfoActivity : AppCompatActivity() {

    companion object {
        public val KEY_LIFE_EXPECTANCY = "KEY_LIFE_EXPECTANCY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demographic_info)

        var lifeExpNum: Int = -1

        btnCalculateLifeXP.setOnClickListener {
            var call = calculateLifeXP()
            call.enqueue(object : Callback<LifeExpectancyResult> {
                override fun onResponse(call: Call<LifeExpectancyResult>, response: Response<LifeExpectancyResult>) {
                    val lifeExpResponse = response.body()?.total_life_expectancy
                    if(lifeExpResponse != null) {
                        lifeExpNum = lifeExpResponse.toInt()
                        tvLifeExpNum.text = lifeExpNum.toString()
                        btnContinue.visibility = View.VISIBLE
                        uploadLifeExpDem()
                    }
                }
                override fun onFailure(call: Call<LifeExpectancyResult>, t: Throwable) {
                    tvLifeExpNum.text = t.message
                }
            })
        }

        btnContinue.setOnClickListener {
                var intentDetails = Intent()
                intentDetails.setClass(this@DemographicInfoActivity,
                    HabitInfoActivity::class.java)

                intentDetails.putExtra(KEY_LIFE_EXPECTANCY, lifeExpNum)
                startActivity(intentDetails)
        }
    }

    fun calculateLifeXP() : Call<LifeExpectancyResult> {
        var retrofit = Retrofit.Builder().baseUrl("http://54.72.28.201").addConverterFactory(GsonConverterFactory.create()).build()

        var lifeExpectancyAPI = retrofit.create(LifeExpectancyAPI::class.java)
        var sexButton : RadioButton = findViewById(radioSex.checkedRadioButtonId)
        val call = lifeExpectancyAPI.getLifeExpectancyDetails(
            sexButton.text.toString(),
            etCountry.text.toString(),
            etBirthday.text.toString() )
        return call
    }

    override fun onBackPressed() {
        FirebaseAuth.getInstance().signOut()
        super.onBackPressed()
    }

    fun uploadLifeExpDem() {
        Log.v("NIKITA", "GETTING TO uploadLifeExpDem")
        val demPost = DemographicPost(
            FirebaseAuth.getInstance().currentUser!!.uid,
            tvLifeExpNum.text.toString()
        )

        var demPostsDatabase = FirebaseFirestore.getInstance().collection(
            "demographics"
        )

        demPostsDatabase.add(
            demPost
        ).addOnSuccessListener {
            Toast.makeText(
                this@DemographicInfoActivity,
                "Life Expectancy Number Saved", Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener {
            Toast.makeText(
                this@DemographicInfoActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }
    }


}
