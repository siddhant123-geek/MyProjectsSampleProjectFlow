package com.example.palisisflow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.palisisag.pitapp.databinding.LayoutMyListBinding

class MyListActivity: AppCompatActivity() {

    private lateinit var binding: LayoutMyListBinding
    private lateinit var adapter: MyAdapter
    private val array = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("###", "onCreate: Coming to MyListActivity")
        binding = LayoutMyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        for(i in 1..10) {
            array.add("My Item $i")
        }
        Log.d("###", "onCreate: array " + array.size)
        setupUi()
    }

    private fun setupUi() {
        val recyclerView = binding.myRecyclerView
        Log.d("###", "onCreate: array " + array.size)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(array)
        Log.d("###", "setupUi: Adapter item count " + adapter.itemCount)
//        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
    }
}