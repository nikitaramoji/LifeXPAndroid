package hu.ait.lifexp

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_life_expectancy_chart.*
import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.components.XAxis
import java.util.*
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.components.YAxis.AxisDependency

class LifeExpectancyChartActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_expectancy_chart)

        setTitle("Life Expectancy Progress")

        val entries = ArrayList<Entry>()
        val db = FirebaseFirestore.getInstance()
        db.collection(
            "users"
        ).document(FirebaseAuth.getInstance().currentUser!!.uid).collection("expectancies"
        ).get(
        ).addOnSuccessListener {documents ->
            for (dc in documents) {
                if(dc["lifeExpectancy"] != null && dc["date"] != null) {
                    var date = dc["date"] as Timestamp
                    var lifeExpectancy = dc["lifeExpectancy"].toString().toFloat()
                    entries.add(Entry(date.seconds.toFloat(), lifeExpectancy))
                }
            }
            createChart(entries)
        }

    }

    fun createChart(entries: ArrayList<Entry>) {
        Log.d("NIKITA", entries.toString())

        // no description text
        chart.getDescription().setEnabled(false);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);

        val l = chart.legend
        l.isEnabled = false

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 20f
        xAxis.textColor = Color.BLUE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        xAxis.resetAxisMinimum()
        xAxis.textColor = Color.rgb(255, 192, 56)
        xAxis.setCenterAxisLabels(true)

        val leftAxis = chart.axisLeft
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.yOffset = -9f
        leftAxis.textColor = Color.rgb(255, 192, 56)

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        var lineDataSet = LineDataSet(entries, "Life Expectancy Progress")

        lineDataSet.axisDependency = AxisDependency.LEFT
        lineDataSet.color = ColorTemplate.getHoloBlue()
        lineDataSet.valueTextColor = ColorTemplate.getHoloBlue()
        lineDataSet.lineWidth = 1.5f
        lineDataSet.setCircleRadius(10f);
        lineDataSet.setDrawCircles(true)
        chart.setPinchZoom(true);
        lineDataSet.setDrawValues(true)
        lineDataSet.setDrawCircles(true)
        lineDataSet.setColor(Color.TRANSPARENT)
        lineDataSet.setCircleColor(Color.BLUE)
        lineDataSet.fillAlpha = 65
        lineDataSet.fillColor = ColorTemplate.getHoloBlue()
        lineDataSet.highLightColor = Color.rgb(244, 117, 117)
        lineDataSet.setDrawCircleHole(false)

        val data = LineData(lineDataSet)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        chart.data = data
        chart.invalidate()
    }
}
