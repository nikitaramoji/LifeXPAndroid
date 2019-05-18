package hu.ait.lifexp

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_life_expectancy_chart.*
import com.github.mikephil.charting.data.Entry
import com.google.firebase.auth.FirebaseAuth
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FirebaseFirestore
import javax.xml.datatype.DatatypeConstants.HOURS
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.Legend
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.components.YAxis.AxisDependency






class LifeExpectancyChartActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_expectancy_chart)

        setTitle("Life Expectancy Progress");

        val entries = ArrayList<Entry>()
        val db = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
        db.collection(
            "users"
        ).document(FirebaseAuth.getInstance().currentUser!!.uid).collection("expectancies"
        ).get(
        ).addOnSuccessListener {documents ->
            entries.clear()
            for (dc in documents) {
                if(dc["lifeExpectancy"] != null && dc["date"] != null) {
                    var date: Timestamp = dc["date"] as Timestamp
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

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

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
        xAxis.granularity = 1f // one hour
        xAxis.valueFormatter = object : ValueFormatter() {

            private val mFormat = SimpleDateFormat("dd MMM HH:mm", Locale.ENGLISH)

            override fun getFormattedValue(value: Float): String {

                val millis = TimeUnit.HOURS.toMillis(value.toLong())
                return mFormat.format(Date(millis))
            }
        }

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
        lineDataSet.fillAlpha = 65
        lineDataSet.fillColor = ColorTemplate.getHoloBlue()
        lineDataSet.highLightColor = Color.rgb(244, 117, 117)
        lineDataSet.setDrawCircleHole(false)

        val data = LineData(lineDataSet)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        chart.data = data
        chart.animateX(2000)
        chart.zoomOut()
        chart.invalidate()
    }
}
