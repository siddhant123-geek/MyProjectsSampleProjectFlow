package com.example.palisisflow

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.palisisag.pitapp.databinding.ItemListBinding

class MyAdapter(private val array: ArrayList<String>):
    Adapter<MyAdapter.DataClassViewHolder>() {

    class DataClassViewHolder(private val binding: ItemListBinding):
            RecyclerView.ViewHolder(binding.root) {
        fun bind(myItem: String, itemClickListener: (String) -> Unit) {
            binding.deviceName.text = myItem
            binding.root.setOnClickListener {
                itemClickListener(myItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataClassViewHolder {
        return DataClassViewHolder(
            ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: DataClassViewHolder, position: Int) {
        val itemData = array[position]
        Log.d("###", "onBindViewHolder: itemdata in Adapter $itemData")
        holder.bind(itemData) { data ->
            Log.d(
                "###",
                "onBindViewHolder: I have clicked on $data"
            )
        }
    }
}