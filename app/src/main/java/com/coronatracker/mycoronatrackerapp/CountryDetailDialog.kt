package com.coronatracker.mycoronatrackerapp

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.coronatracker.mycoronatrackerapp.model.Data
import kotlinx.android.synthetic.main.country_detail_dialog.*


/* Created by *-----* Alexandre Thauvin *-----* */

class CountryDetailDialog(): DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.country_detail_dialog, container)
        getDialog()!!.getWindow()!!.setBackgroundDrawableResource(R.drawable.background_column);
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //tvCancel.setOnClickListener { deleteDialogListener.cancel() }
    }

    override fun onResume() {
        super.onResume()
        val width = resources.getDimensionPixelSize(R.dimen.width_country_detail_popup)
        val height = resources.getDimensionPixelSize(R.dimen.height_country_detail_popup)
        dialog!!.window!!.setLayout(width, height)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity!!.layoutInflater
        val v: View = inflater.inflate(R.layout.country_detail_dialog, null)

        val bundle = arguments
        val data = bundle!!.getParcelable<Data>("data")

        v.findViewById<TextView>(R.id.tvCountry).text = data!!.country
        v.findViewById<TextView>(R.id.tvTotalConfirmedNumber).text = data.cases.toString()
        v.findViewById<TextView>(R.id.tvTotalDeathsNumber).text = data.deaths.toString()
        v.findViewById<TextView>(R.id.tvTotalRecoveredNumber).text = data.recovered.toString()
        v.findViewById<TextView>(R.id.tvTodayConfirmedNumber).text = data.todayCases.toString()
        v.findViewById<TextView>(R.id.tvTodayDeathsNumber).text = data.todayDeaths.toString()
        v.findViewById<TextView>(R.id.tvCriticalNumber).text = data.critical.toString()
        v.findViewById<TextView>(R.id.tvActiveNumber).text = data.active.toString()

        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        builder.setView(v)
        return builder.create()
    }
}