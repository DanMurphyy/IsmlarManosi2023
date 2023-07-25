package com.hfad.ismlarmanosi2023.fragments.names

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.ismlarmanosi2023.data.NamesData
import com.hfad.ismlarmanosi2023.databinding.RowLayoutListBinding

class NamesAdapter : RecyclerView.Adapter<NamesAdapter.MyViewHolder>() {

    var dataList = emptyList<NamesData>()

    class MyViewHolder(internal val binding: RowLayoutListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RowLayoutListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]
        val binding = holder.binding

        binding.tvName.text = currentItem.name
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(namesData: List<NamesData>) {
        this.dataList = namesData
        notifyDataSetChanged()
    }

}