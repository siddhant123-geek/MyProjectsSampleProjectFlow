package com.example.palisisflow
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.palisisag.pitapp.R

abstract class DialogList(
    context: Context,
    private var list: ArrayList<String>,
) : Dialog(context) {

    private var adapter: DevicesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        val view = LayoutInflater.from(context).inflate(R.layout.devices_available_activity, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        view.findViewById<RecyclerView>(R.id.devicesList).layoutManager = LinearLayoutManager(context)
        adapter = DevicesAdapter(list)
        view.findViewById<RecyclerView>(R.id.devicesList).adapter = adapter
    }
}
