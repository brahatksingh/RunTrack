package com.brahatksingh.runtrack.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.brahatksingh.runtrack.R
import com.brahatksingh.runtrack.db.Run
import com.brahatksingh.runtrack.other.TrackingUtility
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdaper : RecyclerView.Adapter<RunAdaper.RunViewHolder>() {

    val diffCallBack = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    val differ = AsyncListDiffer(this,diffCallBack)

    fun submitList(list : List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_run,parent,false))
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(run.img).into(ivRunImage)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy",Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)
            val avgSpeed = "${run.avgSpeedInKMH} km/h"
            tvAvgSpeed.text = avgSpeed

            val distanceInKiloMetrs = "${run.distanceInMeters/1000f}km"
            tvDistance.text = distanceInKiloMetrs

            tvTime.text = TrackingUtility.getFormatted(run.timeInMillis)

            val calories = "${run.caloriesBurned} kcal"

            tvCalories.text = calories
        }
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    inner class RunViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }
}