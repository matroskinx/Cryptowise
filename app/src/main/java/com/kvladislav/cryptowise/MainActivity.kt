package com.kvladislav.cryptowise

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupChart()


        candle_stick_chart.data = prepareMockCandleData()
    }

    fun prepareMockCandleData(): CandleData {
        val entries: ArrayList<CandleEntry> = ArrayList();

        entries.add(CandleEntry(1f, 10f, 2f, 4f, 6f))
        entries.add(CandleEntry(2f, 10f, 2f, 2f, 10f))
        entries.add(CandleEntry(3f, 14f, 0f, 10f, 6f))
        entries.add(CandleEntry(4f, 20f, 8f, 12f, 14f))
        entries.add(CandleEntry(5f, 16f, 2f, 14f, 6f))

        val dataSet1 = CandleDataSet(entries, "Low|High")

        dataSet1.setDrawIcons(false)
        dataSet1.axisDependency = YAxis.AxisDependency.LEFT
        dataSet1.shadowColor = Color.DKGRAY
        dataSet1.shadowWidth = 0.7f
        dataSet1.decreasingColor = Color.RED
        dataSet1.decreasingPaintStyle = Paint.Style.FILL
        dataSet1.increasingColor = Color.rgb(122, 242, 84)
        dataSet1.increasingPaintStyle = Paint.Style.FILL_AND_STROKE
        dataSet1.neutralColor = Color.BLUE

        return CandleData(dataSet1)
    }

    fun setupChart() {
        candle_stick_chart.setBackgroundColor(Color.WHITE)
        candle_stick_chart.description.isEnabled = false
        candle_stick_chart.setMaxVisibleValueCount(60)
        candle_stick_chart.setPinchZoom(false)
        candle_stick_chart.setDrawGridBackground(false)
        val xAxis = candle_stick_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        val leftAxis = candle_stick_chart.axisLeft
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis = candle_stick_chart.axisRight
        rightAxis.isEnabled = false

        candle_stick_chart.legend.isEnabled = false
    }
}
