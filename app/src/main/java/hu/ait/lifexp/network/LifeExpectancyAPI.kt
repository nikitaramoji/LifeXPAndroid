package hu.ait.lifexp.network

import hu.ait.lifexp.data.LifeExpectancyResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface LifeExpectancyAPI {
    @GET("/1.0/life-expectancy/total/{sex}/{country}/{dob}")
    fun getLifeExpectancyDetails(@Path("sex") sex: String,
                          @Path("country") country: String,
                          @Path("dob") dob: String): Call<LifeExpectancyResult>
}