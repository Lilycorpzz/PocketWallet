package com.example.pocketwallet

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class CategoryLabelFormatter(
    private val labels: List<String>
) : IndexAxisValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return if (value.toInt() in labels.indices) {
            labels[value.toInt()]
        } else {
            ""
        }
    }
}
