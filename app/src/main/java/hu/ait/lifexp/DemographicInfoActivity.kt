package hu.ait.lifexp


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import hu.ait.lifexp.data.LifeExpectancyResult
import hu.ait.lifexp.network.LifeExpectancyAPI
import kotlinx.android.synthetic.main.activity_demographic_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DemographicInfoActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demographic_info)

        btnCalculateLifeXP.setOnClickListener {
            var call = calculateLifeXP()
            call.enqueue(object : Callback<LifeExpectancyResult> {
                override fun onResponse(call: Call<LifeExpectancyResult>, response: Response<LifeExpectancyResult>) {
                    val lifeExpNum = response.body()?.total_life_expectancy
                    if(lifeExpNum != null) {
                        tvLifeExpNum.text = lifeExpNum.toInt().toString()
                    }
                }
                override fun onFailure(call: Call<LifeExpectancyResult>, t: Throwable) {
                    tvLifeExpNum.text = t.message
                }
            })
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


}
