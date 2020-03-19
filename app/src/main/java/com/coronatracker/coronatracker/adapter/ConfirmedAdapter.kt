package com.coronatracker.coronatracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coronatracker.coronatracker.R
import com.coronatracker.coronatracker.model.Data
import kotlinx.android.synthetic.main.confirmed_item.view.*

class ConfirmedAdapter: RecyclerView.Adapter<ConfirmedAdapter.ConfirmedViewHolder>() {

    private val list = ArrayList<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.confirmed_item, parent, false)
        return ConfirmedViewHolder(view)
    }

    override fun onBindViewHolder(feedViewHolder: ConfirmedViewHolder, position: Int) {
        feedViewHolder.update(list[position])
    }

    override fun getItemCount() = list.size

    class ConfirmedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun update(data: Data) = with(itemView) {
            tvCountryConfirmed.text = data.country
            tvNumberConfirmed.text = data.cases.toString()
        }
    }

    fun update(listData: List<Data>) {
        list.clear()
        list.addAll(listData)
        notifyDataSetChanged()
    }
}