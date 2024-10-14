package com.pmcmaApp.pmcma

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NGOAdapter(private val context: Context, private val ngoList: List<NGO>) :
    RecyclerView.Adapter<NGOAdapter.NGOViewHolder>() {

    inner class NGOViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ngoName: TextView = itemView.findViewById(R.id.ngo_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                val selectedNGO = ngoList[position]
                val intent = Intent(context, NGODetailActivity::class.java).apply {
                    putExtra("ngo", selectedNGO)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NGOViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_ngo, parent, false)
        return NGOViewHolder(view)
    }

    override fun onBindViewHolder(holder: NGOViewHolder, position: Int) {
        val ngo = ngoList[position]
        holder.ngoName.text = ngo.name
    }

    override fun getItemCount(): Int {
        return ngoList.size
    }
}
