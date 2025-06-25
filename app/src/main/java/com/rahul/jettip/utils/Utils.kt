package com.rahul.jettip.utils

object Utils {

    fun calculateTotalTip(totalBill: Double?, tipPercentage: Int): Double {
        return if ((totalBill ?: 0.0) > 1 && totalBill.toString().isNotEmpty())
            ((totalBill ?: 0.0) * tipPercentage) / 100 else 0.0
    }

    fun calculateTotalPerPerson(totalBill: Double?, tipPercentage: Int, splitBy: Int): Double {
        val totalTip = calculateTotalTip(totalBill, tipPercentage)
        return if ((totalBill ?: 0.0) > 1 && totalBill.toString().isNotEmpty() && splitBy > 0)
            ((totalBill ?: 0.0) + totalTip) / splitBy else 0.0
    }
}