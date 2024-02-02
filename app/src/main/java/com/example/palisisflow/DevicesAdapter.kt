package com.example.palisisflow

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.palisisag.pitapp.databinding.ItemListBinding

class DevicesAdapter(private val devicesList: ArrayList<String>):
    RecyclerView.Adapter<DevicesAdapter.DataViewHolder>() {

    class DataViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("MissingPermission")
        fun bind(device: String) {
            binding.deviceName.text = device
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            ItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    override fun getItemCount(): Int {
        return devicesList.size;
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(devicesList[position])
    }

//    fun addData(list: List<String>) {
//        devicesList.addAll(list)
//    }

    fun addDevice(device: String) {
        devicesList.add(device)
    }
}