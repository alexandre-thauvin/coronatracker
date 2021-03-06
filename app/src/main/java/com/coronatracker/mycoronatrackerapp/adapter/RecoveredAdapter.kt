package com.coronatracker.mycoronatrackerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coronatracker.mycoronatrackerapp.R
import com.coronatracker.mycoronatrackerapp.model.Data
import kotlinx.android.synthetic.main.recovered_item.view.*

class RecoveredAdapter(private val clickListener: (Data) -> Unit): RecyclerView.Adapter<RecoveredAdapter.ConfirmedViewHolder>() {

    private val list = ArrayList<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recovered_item, parent, false)
        return ConfirmedViewHolder(view)
    }

    override fun onBindViewHolder(feedViewHolder: ConfirmedViewHolder, position: Int) {
        feedViewHolder.update(list[position], clickListener)
    }

    override fun getItemCount() = list.size

    class ConfirmedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun update(data: Data, clickListener: (Data) -> Unit) = with(itemView) {
            tvCountryRecovered.text = data.country
            tvNumberRecovered.text = data.recovered.toString()
            rlRecovered.setOnClickListener {  _ -> clickListener(data) }
        }
    }

    fun update(listData: List<Data>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }
}